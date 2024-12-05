package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.dto.*;
import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamCentreService;
import com.cdac.exambackup.util.CsvUtil;
import com.cdac.exambackup.util.NullAndBlankUtil;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamCentreServiceImpl extends AbstractBaseService<ExamCentre, Long> implements ExamCentreService {
    static final String NOT_FOUND = " not found";
    static final String[] HEADER = {"Center_Code", "Name", "Region", "Mobile", "Email"};
    final ApplicationContext applicationContext;
    final ExamCentreDao examCentreDao;
    final ExamFileDao examFileDao;
    final FileTypeDao fileTypeDao;
    final RegionDao regionDao;
    final RoleDao roleDao;
    final AppUserDao appUserDao;
    final PasswordEncoder passwordEncoder;
    final ExamDao examDao;
    final ExamSlotDao examSlotDao;
    final ExamDateDao examDateDao;
    final SlotDao slotDao;

    public ExamCentreServiceImpl(BaseDao<ExamCentre, Long> baseDao, ExamFileDao examFileDao, ExamCentreDao examCentreDao, FileTypeDao fileTypeDao, RegionDao regionDao, RoleDao roleDao, AppUserDao appUserDao, PasswordEncoder passwordEncoder, ExamDao examDao, ExamSlotDao examSlotDao, ExamDateDao examDateDao, SlotDao slotDao, ApplicationContext applicationContext) {
        super(baseDao);
        this.examFileDao = examFileDao;
        this.examCentreDao = examCentreDao;
        this.fileTypeDao = fileTypeDao;
        this.regionDao = regionDao;
        this.roleDao = roleDao;
        this.appUserDao = appUserDao;
        this.passwordEncoder = passwordEncoder;
        this.examDao = examDao;
        this.examSlotDao = examSlotDao;
        this.examDateDao = examDateDao;
        this.slotDao = slotDao;
        this.applicationContext = applicationContext;
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
            Util.isConvertibleToNumberElseThrowException("code", examCentreReqDto.code());
            ExamCentre daoExamCentre = examCentreDao.findByCode(examCentreReqDto.code());
            if (daoExamCentre != null) {
                throw new InvalidReqPayloadException("Same code: " + examCentreReqDto.code() + " already exists");
            }

            Region daoRegion = regionDao.findByName(examCentreReqDto.regionName().trim());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreReqDto.regionName() + NOT_FOUND);
            }

            /* before saving need to create a User account for this exam centre for login into the system.
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
            appUser.setIsRegionHead(false);
            appUser.setRegionId(daoRegion.getId());
            // TODO: mobile and email mandatory or not to be decided
            if (examCentreReqDto.email() != null && !examCentreReqDto.email().isBlank()) {
                if (!Util.validateEmail(examCentreReqDto.email())) {
                    throw new InvalidReqPayloadException("Malformed email address: " + examCentreReqDto.email());
                }
                appUser.setEmail(examCentreReqDto.email());
            }
            if (examCentreReqDto.mobileNumber() != null && !examCentreReqDto.mobileNumber().isBlank()) {
                if (!Util.validateMobileNumber(examCentreReqDto.mobileNumber())) {
                    throw new InvalidReqPayloadException("Malformed mobile number: " + examCentreReqDto.mobileNumber());
                }
                appUser.setMobileNumber(examCentreReqDto.mobileNumber());
            }

            Role daoRole = roleDao.findByName("USER"); // default role
            if (daoRole == null) {
                throw new EntityNotFoundException("Role with name: 'USER' not found");
            }
            appUser.setRole(daoRole);
            appUserDao.save(appUser);

            // now remove the unnecessary fields if present or create a new object.
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
            1. if examCentreId is updated, then userId is also to be updated
            2. if examCentreName is updated, then the userName is also to be updated
            3. password need to be reset ?? no
        */

        ExamCentre daoExamCentre = examCentreDao.findById(examCentreReqDto.id());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examCentreReqDto.id() + " not found.");
        }

        String oldExamCode = daoExamCentre.getCode();

        // if all values are invalid, one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(examCentreReqDto.code(), examCentreReqDto.name()) && examCentreReqDto.regionName() == null && examCentreReqDto.email() == null && examCentreReqDto.mobileNumber() == null && examCentreReqDto.examDateSlots() == null) {
            throw new InvalidReqPayloadException("All 'code', 'name', 'regionName', 'email', 'mobileNumber' and 'examDateSlot' cannot be null");
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
            Util.isConvertibleToNumberElseThrowException("code", examCentreReqDto.code());
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
                throw new EntityNotFoundException("Region with id: " + examCentreReqDto.regionName() + NOT_FOUND);
            }
            daoExamCentre.setRegion(daoRegion);
            daoAppUser.setRegionId(daoRegion.getId());
        }

        if (examCentreReqDto.email() != null && !examCentreReqDto.email().isBlank()) {
            if (!Util.validateEmail(examCentreReqDto.email())) {
                throw new InvalidReqPayloadException("Malformed email address: " + examCentreReqDto.email());
            }
            daoAppUser.setEmail(examCentreReqDto.email().trim());
        }
        if (examCentreReqDto.mobileNumber() != null && !examCentreReqDto.mobileNumber().isBlank()) {
            if (!Util.validateMobileNumber(examCentreReqDto.mobileNumber())) {
                throw new InvalidReqPayloadException("Malformed mobile number: " + examCentreReqDto.mobileNumber());
            }
            daoAppUser.setMobileNumber(examCentreReqDto.mobileNumber());
        }
        appUserDao.save(daoAppUser);
        ExamCentre savedExamCentre = examCentreDao.save(daoExamCentre);
        saveExamAndExamSlot(savedExamCentre, examCentreReqDto.examDateSlots());
        return savedExamCentre;
    }

    private void saveExamAndExamSlot(ExamCentre examCentre, Set<ExamDateSlot> examDateSlots) {
        if (examDateSlots != null) {
            // delete previous exams and slots
            List<Long> examIds = examDao.findIdsByExamCentreId(examCentre.getId());
            examSlotDao.deleteByExamIdIn(examIds);
            examDao.deleteByExamCentre(examCentre);

            examDateSlots.forEach(examDateSlot -> {
                ExamDate daoExamDate = examDateDao.findById(examDateSlot.examDateId());
                if (daoExamDate == null) {
                    throw new EntityNotFoundException("ExamDate with id: " + examDateSlot.examDateId() + NOT_FOUND);
                }
                Exam savedExam = examDao.save(new Exam(examCentre, daoExamDate));
                List<ExamSlot> examSlots = new ArrayList<>();
                for (Long slotId : examDateSlot.slotIds()) {
                    Slot daoSlot = slotDao.findById(slotId);
                    if (daoSlot == null) {
                        throw new EntityNotFoundException("Slot with id: " + slotId + NOT_FOUND);
                    }
                    ExamSlot daoExamSlot = new ExamSlot(savedExam, daoSlot);
                    examSlots.add(daoExamSlot);
                }
                examSlotDao.save(examSlots);
            });
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> getByQueryOrCodeOrNameOrRegionId(String query, String code, String name, Long regionId, Pageable pageable) {
        Page<ExamCentre> examCentrePage;
        if (code != null && name != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndCodeAndName(regionId, code, name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (code != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndCode(regionId, code, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (name != null && regionId != null) {
            examCentrePage = examCentreDao.findByRegionIdAndName(regionId, name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (query != null && !query.isBlank() && regionId != null) {
            examCentrePage = examCentreDao.searchWithRegionId(query, regionId, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (code != null) {
            examCentrePage = examCentreDao.findByCode(code, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (name != null) {
            examCentrePage = examCentreDao.findByName(name, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        } else if (regionId != null) {
            examCentrePage = examCentreDao.findByRegionId(regionId, pageable);
            return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), convertExamCentresToExamCentreResDto(examCentrePage.getContent()));
        }
        return new PageResDto<>(0, 0, 0, 0, Collections.emptyList());

    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> searchByQueryAndRegionId(String query, Long regionId, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return new PageResDto<>(0, 0, 0, 0, Collections.emptyList());
        }
        Page<ExamCentre> examCentrePage;
        if (regionId != null) {
            examCentrePage = examCentreDao.searchWithRegionId(query, regionId, pageable);
        } else {
            examCentrePage = examCentreDao.search(query, pageable);
        }
        return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), getExamCentreWithExamDateSlots(examCentrePage));
    }

    @Override
    public PageResDto<List<ExamCentreResDto>> getAllByPage(Pageable pageable) {
        Page<ExamCentre> examCentrePage = examCentreDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), examCentrePage.getNumberOfElements(), examCentrePage.getTotalElements(), examCentrePage.getTotalPages(), getExamCentreWithExamDateSlots(examCentrePage));
    }

    private List<ExamCentreResDto> getExamCentreWithExamDateSlots(Page<ExamCentre> examCentrePage) {
        return examCentrePage.getContent().stream().map(examCentre -> {
            AppUser appUser = appUserDao.findByUserId(examCentre.getCode());
            if (appUser == null) {
                throw new EntityNotFoundException("AppUser with userId: " + examCentre.getCode() + NOT_FOUND);
            }
            Set<ExamDateSlot> examDateSlots = examDao.findByExamCentreId(examCentre.getId())
                    .stream()
                    .map(exam -> new ExamDateSlot(exam.getExamDate().getId(), examSlotDao.findByExamId(exam.getId())
                            .stream()
                            .map(examSlot -> examSlot.getSlot().getId())
                            .collect(Collectors.toSet()))
                    ).collect(Collectors.toSet());
            return new ExamCentreResDto(examCentre.getId(), examCentre.getCode(), examCentre.getName(), examCentre.getRegion().getName(), appUser.getMobileNumber(), appUser.getEmail(), null, null, examDateSlots, examCentre.getCreatedDate(), examCentre.getModifiedDate());
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamCentreResDto>> getExamCentresOnUploadStatusByPage(String query, String filterType, Long regionId, Pageable pageable) {
        List<ExamCentreResDto> examCentreResDtos;
        if ("UPLOADED".equalsIgnoreCase(filterType)) {
            examCentreResDtos = convertExamCentresToExamCentreResDto(examCentreDao.findByRegionId(regionId))
                    .stream()
                    .filter(examCentreResDto -> {
                        if (query == null || query.isBlank()) {
                            return isUploadCompleted(examCentreResDto);
                        }
                        return isUploadCompleted(examCentreResDto) && (examCentreResDto.code().toLowerCase().contains(query.trim().toLowerCase()) || examCentreResDto.name().toLowerCase().contains(query.trim().toLowerCase()));
                    })
                    .sorted((a, b) -> sort(pageable, a, b))
                    .toList();


        } else if ("NOT_UPLOADED".equalsIgnoreCase(filterType)) {
            examCentreResDtos = convertExamCentresToExamCentreResDto(examCentreDao.findByRegionId(regionId))
                    .stream()
                    .filter(examCentreResDto -> {
                        if (query == null || query.isBlank()) {
                            return !isUploadCompleted(examCentreResDto);
                        }
                        return !isUploadCompleted(examCentreResDto) && (examCentreResDto.code().toLowerCase().contains(query.trim().toLowerCase()) || examCentreResDto.name().toLowerCase().contains(query.trim().toLowerCase()));
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
                        throw new EntityNotFoundException("AppUser with userId: " + examCentre.getCode() + NOT_FOUND);
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

    @Transactional
    @Override
    public void bulkUpload(MultipartFile csvFile) {
        if (csvFile == null || !CsvUtil.hasCsvFormat(csvFile)) {
            throw new InvalidReqPayloadException("Empty or invalid CSV format.");
        }
        byte[] bytes;
        try {
            bytes = csvFile.getBytes();
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
            throw new InvalidReqPayloadException("Invalid csv payload.");
        }
        String completeData = new String(bytes, StandardCharsets.UTF_8);
        String[] rows = completeData.split("\n");
        String[] header = rows[0].split(",");
        if (HEADER.length != header.length) {
            throw new InvalidReqPayloadException("Invalid csv header; Should be- " + Arrays.toString(HEADER));
        }

        var examCentreService = applicationContext.getBean(ExamCentreService.class);

        for (int i = 1; i < rows.length; i++) {
            if (!rows[i].isBlank()) {
                String[] row = rows[i].split(",");
                if (HEADER.length != row.length) {
                    throw new InvalidReqPayloadException("Invalid csv row at index " + i + "; Should be- " + Arrays.toString(HEADER));
                }
                // remove double quotes in the ends
                for (int j = 0; j < row.length; j++) {
                    if (row[j].startsWith("\"") && row[j].endsWith("\"")) {
                        row[j] = row[j].substring(1, row[j].length() - 1);
                    }
                }
                // "Center_Code", "Name", "Region", "Mobile", "Email"
                var examCentreReqDto = new ExamCentreReqDto(null, row[0].trim(), row[1].trim(), row[2].trim(), row[3].trim(), row[4].trim(), null);
                examCentreService.save(examCentreReqDto);
            }
        }
    }

    @Transactional
    @Override
    public void updateOnlySlot(ExamCentreSlotUpdateReqDto examCentreSlotUpdateReqDto) {
        if (examCentreSlotUpdateReqDto.examCentreIds() == null || examCentreSlotUpdateReqDto.examDateIds() == null || examCentreSlotUpdateReqDto.slotIds() == null) {
            throw new InvalidReqPayloadException("All 'examCentreIds', 'examDateIds' and 'slotIds' cannot be null.");
        }
        if (examCentreSlotUpdateReqDto.examCentreIds().isEmpty() || examCentreSlotUpdateReqDto.examDateIds().isEmpty() || examCentreSlotUpdateReqDto.slotIds().isEmpty()) {
            throw new InvalidReqPayloadException("All 'examCentreIds', 'examDateIds' and 'slotIds' cannot be empty.");
        }
        var examCentreService = applicationContext.getBean(ExamCentreService.class);
        examCentreSlotUpdateReqDto.examCentreIds().forEach(examCentreId -> {
            Set<ExamDateSlot> examDateSlots = new HashSet<>();
            examCentreSlotUpdateReqDto.examDateIds().forEach(examDateId -> examDateSlots.add(new ExamDateSlot(examDateId, examCentreSlotUpdateReqDto.slotIds())));
            var examCentreReqDto = new ExamCentreReqDto(examCentreId, null, null, null, null, null, examDateSlots);
            examCentreService.save(examCentreReqDto);
        });
    }

    @Override
    public List<ExamCentreResDto> getAllByRegionIds(List<Long> regionIds) {
        return examCentreDao.getAllByRegionIds(regionIds).stream().map(examCentre -> new ExamCentreResDto(examCentre.getId(), examCentre.getCode(), examCentre.getName(), null, null, null, null, null, null, null, null)).toList();
    }
}
