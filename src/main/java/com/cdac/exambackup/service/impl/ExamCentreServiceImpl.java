package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.dto.ExamCentreReqDto;
import com.cdac.exambackup.dto.ExamCentreResDto;
import com.cdac.exambackup.dto.ExamDateSlot;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamCentreService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamCentreServiceImpl extends AbstractBaseService<ExamCentre, Long> implements ExamCentreService {
    @Autowired
    ExamCentreDao examCentreDao;

    @Autowired
    ExamFileDao examFileDao;

    @Autowired
    FileTypeDao fileTypeDao;


    @Autowired
    RegionDao regionDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ExamDao examDao;
    @Autowired
    ExamSlotDao examSlotDao;

    @Autowired
    ExamDateDao examDateDao;
    @Autowired
    SlotDao slotDao;

    public ExamCentreServiceImpl(BaseDao<ExamCentre, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamCentre save(ExamCentreReqDto examCentreReqDto) {
        // new record entry
        if (examCentreReqDto.id() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(examCentreReqDto.code(), examCentreReqDto.name()) || examCentreReqDto.regionName() == null) {
                throw new InvalidReqPayloadException("All 'code','name', and 'regionName' cannot be null or empty.");
            }
            ExamCentre daoExamCentre = examCentreDao.findByCode(examCentreReqDto.code());
            if (daoExamCentre != null) {
                throw new InvalidReqPayloadException("Same 'code' already exists");
            }

            Region daoRegion = regionDao.findByName(examCentreReqDto.regionName());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreReqDto.regionName() + " not found");
            }

            /* before saving need to create User account for this exam centre for login into system.
                1. create a user:
                        1. with role as `USER`
                        2. userId as examCentreId
                        3. name as ExamCentreName
                        4. password as userId encrypted using BCryptEncoder
                        4. the rest used default values
             */

            AppUser appUser = new AppUser();
            appUser.setName(examCentreReqDto.name().trim());
            appUser.setUserId(examCentreReqDto.code().trim());
            appUser.setPassword(passwordEncoder.encode(examCentreReqDto.code().trim()));
            // TODO: mobile and email mandatory or not to be decided
            if (examCentreReqDto.email() != null && !examCentreReqDto.email().isBlank()) {
                appUser.setEmail(examCentreReqDto.email());
            }
            if (examCentreReqDto.mobileNumber() != null && !examCentreReqDto.mobileNumber().isBlank()) {
                appUser.setMobileNumber(examCentreReqDto.mobileNumber());
            }

            Role daoRole = roleDao.findByName("USER"); // default role
            if (daoRole == null) {
                throw new EntityNotFoundException("Role with name: 'USER' not found");
            }
            appUser.setRole(daoRole);
            appUserDao.save(appUser);

            // now remove the unnecessary fields if present or create new object.
            ExamCentre examCentre = new ExamCentre();
            examCentre.setCode(examCentreReqDto.code().trim());
            examCentre.setName(examCentreReqDto.name().trim());
            examCentre.setRegion(daoRegion);

            ExamCentre savedExamCentre = examCentreDao.save(examCentre);
            saveExamAndExamSlot(savedExamCentre, examCentreReqDto.examDateSlots());
            return savedExamCentre;
        }
        // for updating existing record.

        /*
            1. if examCentreId is updated then userId is also to be updated
            2. if examCentreName is updated then userName is also to be updated
            3. password need to be reset ?? no
        */

        ExamCentre daoExamCentre = examCentreDao.findById(examCentreReqDto.id());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examCentreReqDto.id() + " not found.");
        }

        String oldExamCode = daoExamCentre.getCode();

        // if both values are invalid, one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(examCentreReqDto.code(), examCentreReqDto.name()) && examCentreReqDto.regionName() == null) {
            throw new InvalidReqPayloadException("All 'code', 'name' and 'regionName' cannot be null");
        }

        // if examCentre is changed, then userId & name must also be changed accordingly
        AppUser daoAppUser = appUserDao.findByUserId(oldExamCode);
        if (daoAppUser == null) {
            throw new EntityNotFoundException("User with userId: " + oldExamCode + " not found.");
        }

        if (examCentreReqDto.code() != null) {
            if (examCentreReqDto.code().isBlank()) {
                throw new InvalidReqPayloadException("code is empty.");
            }
            daoExamCentre.setCode(examCentreReqDto.code());
            daoAppUser.setUserId(daoExamCentre.getCode()); // also change userId
        }
        if (examCentreReqDto.name() != null) {
            if (examCentreReqDto.name().isBlank()) {
                throw new InvalidReqPayloadException("Name is empty.");
            }
            daoExamCentre.setName(examCentreReqDto.name());
            daoAppUser.setName(daoExamCentre.getName());
        }
        if (examCentreReqDto.regionName() != null) {
            Region daoRegion = regionDao.findByName(examCentreReqDto.regionName());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreReqDto.regionName() + " not found");
            }
            daoExamCentre.setRegion(daoRegion);
        }

        if (examCentreReqDto.email() != null && !examCentreReqDto.email().isBlank()) {
            daoAppUser.setEmail(examCentreReqDto.email());
        }
        if (examCentreReqDto.mobileNumber() != null && !examCentreReqDto.mobileNumber().isBlank()) {
            daoAppUser.setMobileNumber(examCentreReqDto.mobileNumber());
        }
        appUserDao.save(daoAppUser);
        ExamCentre savedExamCentre = examCentreDao.save(daoExamCentre);
        saveExamAndExamSlot(savedExamCentre, examCentreReqDto.examDateSlots());
        return savedExamCentre;
    }

    private void saveExamAndExamSlot(ExamCentre examCentre, List<ExamDateSlot> examDateSlots) {
        if (examDateSlots != null) {
            examDateSlots.forEach(examDateSlot -> {
                ExamDate daoExamDate = examDateDao.findById(examDateSlot.examDateId());
                if (daoExamDate == null) {
                    throw new EntityNotFoundException("ExamDate with id: " + examDateSlot.examDateId() + " not found");
                }
                Exam daoExam = examDao.findByExamCentreAndExamDate(examCentre, daoExamDate);
                if (daoExam == null) {
                    daoExam = examDao.save(new Exam(examCentre, daoExamDate));
                }
                List<ExamSlot> examSlots = new ArrayList<>();
                for (Long slotId : examDateSlot.slotIds()) {
                    Slot daoSlot = slotDao.findById(slotId);
                    if (daoSlot == null) {
                        throw new EntityNotFoundException("Slot with id: " + slotId + " not found");
                    }
                    ExamSlot daoExamSlot = examSlotDao.findByExamAndSlot(daoExam, daoSlot);
                    if (daoExamSlot == null) {
                        daoExamSlot = new ExamSlot(daoExam, daoSlot);
                    }
                    examSlots.add(daoExamSlot);
                }
                examSlotDao.save(examSlots);
            });
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> getByCodeOrNameOrRegionId(String code, String name, Long regionId, Pageable pageable) {
        Page<ExamCentre> examCentrePage;
        if (code != null && name != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndCodeOrName(regionId, code, name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (code != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndCode(regionId, code, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (name != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndName(regionId, name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (code != null || name != null) {
            examCentrePage = examCentreDao.findByCodeOrName(code, name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (regionId != null) {
            examCentrePage = examCentreDao.findByRegionId(regionId, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        }
        return new PageResDto<>(0, 0, 0, 0, Collections.emptyList());

    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> search(String searchTerm, Long regionId, Pageable pageable) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return new PageResDto<>(0, 0, 0, 0, Collections.emptyList());
        }
        Page<ExamCentre> examCentrePage;
        if (regionId != null) {
            examCentrePage = examCentreDao.searchWithRegionId(searchTerm, regionId, pageable);
        } else {
            examCentrePage = examCentreDao.search(searchTerm, pageable);
        }
        return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> getExamCentresOnUploadStatusByPage(String searchTerm, String filterType, Long regionId, Pageable pageable) {
        List<ExamCentreResDto> examCentreResDtos;
        if ("UPLOADED".equalsIgnoreCase(filterType)) {
            examCentreResDtos = convertExamCentresToExamCentreResDto(examCentreDao.findByRegionId(regionId))
                    .stream()
                    .filter(examCentreResDto -> {
                        if (searchTerm == null || searchTerm.isBlank()) {
                            return isUploadCompleted(examCentreResDto);
                        }
                        return isUploadCompleted(examCentreResDto) && (examCentreResDto.code().toLowerCase().contains(searchTerm.trim().toLowerCase()) || examCentreResDto.name().toLowerCase().contains(searchTerm.trim().toLowerCase()));
                    })
                    .sorted((a, b) -> sort(pageable, a, b))
                    .toList();


        } else if ("NOT_UPLOADED".equalsIgnoreCase(filterType)) {
            examCentreResDtos = convertExamCentresToExamCentreResDto(examCentreDao.findByRegionId(regionId))
                    .stream()
                    .filter(examCentreResDto -> {
                        if (searchTerm == null || searchTerm.isBlank()) {
                            return !isUploadCompleted(examCentreResDto);
                        }
                        return !isUploadCompleted(examCentreResDto) && (examCentreResDto.code().toLowerCase().contains(searchTerm.trim().toLowerCase()) || examCentreResDto.name().toLowerCase().contains(searchTerm.trim().toLowerCase()));
                    })
                    .sorted((a, b) -> sort(pageable, a, b))
                    .toList();
        } else {
            examCentreResDtos = convertExamCentresToExamCentreResDto(examCentreDao.findByRegionId(regionId))
                    .stream()
                    .sorted((a, b) -> sort(pageable, a, b))
                    .toList();
        }

        int totalPages = Math.ceilDiv(examCentreResDtos.size(), pageable.getPageSize());
        int pageNumber = pageable.getPageNumber();
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageNumber > 0) {
            pageNumber = pageNumber * pageable.getPageSize();
        }
        int toIndex = pageNumber + pageable.getPageSize(); // starts from pageNumber index.

        List<ExamCentreResDto> examCentreResDtoContent = examCentreResDtos.subList(Math.min(pageNumber, examCentreResDtos.size()), Math.min(toIndex, examCentreResDtos.size()));
        return new PageResDto<>(
                pageNumber,
                examCentreResDtoContent.size(),
                examCentreResDtos.size(),
                totalPages,
                examCentreResDtoContent);
    }

    @Override
    public PageResDto<List<ExamCentreResDto>> getAllByPage(Pageable pageable) {
        Page<ExamCentre> page = examCentreDao.getAllByPage(pageable);
        List<ExamCentreResDto> examCentreResDto = page.getContent().stream().map(examCentre -> {
            AppUser appUser = appUserDao.findByUserId(examCentre.getCode());
            if (appUser == null) {
                throw new EntityNotFoundException("AppUser with userId: " + examCentre.getCode() + " not found.");
            }
            List<ExamDateSlot> examDateSlots = examDao.findByExamCentreId(examCentre.getId())
                    .stream()
                    .map(exam -> new ExamDateSlot(exam.getExamDate().getId(), examSlotDao.findByExamId(exam.getId())
                            .stream()
                            .map(examSlot -> examSlot.getSlot().getId())
                            .toList())
                    ).toList();
            return new ExamCentreResDto(examCentre.getId(), examCentre.getCode(), examCentre.getName(), examCentre.getRegion().getName(), appUser.getMobileNumber(), appUser.getEmail(), null, null, examDateSlots, examCentre.getCreatedDate(), examCentre.getModifiedDate());
        }).toList();
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), examCentreResDto);
    }

    private static boolean isUploadCompleted(ExamCentreResDto examCentreResDto) {
        return examCentreResDto.totalFileCount().equals(examCentreResDto.uploadedFileCount());
    }

    private static int sort(Pageable pageable, ExamCentreResDto a, ExamCentreResDto b) {
        Sort.Order order = pageable.getSort().getOrderFor("code");
        if (order != null) {
            return order.isAscending() ? a.code().compareTo(b.code()) : b.code().compareTo(a.code());
        }
        order = pageable.getSort().getOrderFor("name");
        if (order != null) {
            return order.isAscending() ? a.name().compareTo(b.name()) : b.name().compareTo(a.name());
        }
        return a.id().compareTo(b.id());
    }

    private List<ExamCentreResDto> convertExamCentresToExamCentreResDto(List<ExamCentre> examCentres) {
        return examCentres
                .stream()
                .map(examCentre -> {
                    AppUser appUser = appUserDao.findByUserId(examCentre.getCode());
                    if (appUser == null) {
                        throw new EntityNotFoundException("AppUser with userId: " + examCentre.getCode() + " not found.");
                    }
                    List<Exam> exams = examDao.findByExamCentreId(examCentre.getId());
                    AtomicLong atomicLong = new AtomicLong(0);
                    exams.forEach(exam -> atomicLong.getAndAdd(examSlotDao.countByExamId(exam.getId())));
                    int numberOfSlots = (int) atomicLong.get();
                    long numberOfFileTypes = fileTypeDao.countNonDeleted();
                    int totalFileCount = (int) (numberOfSlots * numberOfFileTypes);
                    int uploadedFileCount = examFileDao.findByExamCentre(examCentre).size();
                    return new ExamCentreResDto(examCentre.getId(), examCentre.getCode(), examCentre.getName(), examCentre.getRegion().getName(), appUser.getMobileNumber(), appUser.getEmail(), totalFileCount, uploadedFileCount, null, examCentre.getCreatedDate(), examCentre.getModifiedDate());
                }).toList();
    }

}
