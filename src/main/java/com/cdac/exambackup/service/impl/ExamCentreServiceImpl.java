package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.dto.ExamCentreResDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamCentreService;
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

import java.util.Collections;
import java.util.List;

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
    ExamDateDao examDateDao;

    @Autowired
    FileTypeDao fileTypeDao;

    @Autowired
    SlotDao slotDao;

    @Autowired
    RegionDao regionDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ExamCentreServiceImpl(BaseDao<ExamCentre, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamCentre save(ExamCentre examCentreDto) {
         /*
             if id not present in dto:
                  add new record after passing the constraint check (code&name,region)
             else:
                  if entity exist in table for passed id:
                       update only {code} and {name} after passing the constraint check. // other fields have separate API.
                  else:
                       throw exception.
         */

        // new record entry
        if (examCentreDto.getId() == null) {
            // if both values are invalid, throw exception
            if (examCentreDto.getCode() == null || examCentreDto.getCode().isBlank() || examCentreDto.getName() == null || examCentreDto.getName().isBlank()) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or empty.");
            }
            ExamCentre daoExamCentre = examCentreDao.findByCode(examCentreDto.getCode());
            if (daoExamCentre != null) {
                throw new InvalidReqPayloadException("Same 'code' already exists");
            }
            if (examCentreDto.getRegion() == null) {
                throw new EntityNotFoundException("Region cannot be null.");
            }
            Region daoRegion = regionDao.findById(examCentreDto.getRegion().getId());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreDto.getRegion().getId() + " not found");
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
            appUser.setName(examCentreDto.getName());
            appUser.setUserId(examCentreDto.getCode());
            appUser.setPassword(passwordEncoder.encode(examCentreDto.getCode()));

            Role daoRole = roleDao.findByName("USER"); // default role
            if (daoRole == null) {
                throw new EntityNotFoundException("Role with name: 'USER' not found");
            }
            appUser.setRole(daoRole);
            appUserDao.save(appUser);

            // now remove the unnecessary fields if present or create new object.
            ExamCentre examCentre = new ExamCentre();
            examCentre.setCode(examCentreDto.getCode());
            examCentre.setName(examCentreDto.getName().trim());
            examCentre.setRegion(daoRegion);

            return examCentreDao.save(examCentre);
        }
        // for updating existing record.

        // do the validation/constraint check and update

        /*
            1. if examCentreId is updated then userId is also to be updated
            2. if examCentreName is updated then userName is also to be updated
            3. password need to be reset ?? no
        */

        ExamCentre daoExamCentre = examCentreDao.findById(examCentreDto.getId());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examCentreDto.getId() + " not found.");
        }
        if (Boolean.FALSE.equals(daoExamCentre.getActive())) {
            throw new EntityNotFoundException("ExamCentre with id: " + examCentreDto.getId() + " is not active. Must activate first.");
        }

        String oldExamCode = daoExamCentre.getCode();

        // if both values are invalid, one should be valid
        if (examCentreDto.getCode() == null && examCentreDto.getName() == null) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null");
        }

        // if both values are invalid, one should be valid
        if ((examCentreDto.getCode() != null && examCentreDto.getCode().isBlank()) && (examCentreDto.getName() != null && examCentreDto.getName().isBlank())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be  empty");
        }
        // examCentreId is change, so userId & name must also be changed
        // good catch
        AppUser daoAppUser = appUserDao.findByUserId(oldExamCode);
        if (daoAppUser == null) {
            throw new EntityNotFoundException("User with userId: " + oldExamCode + " not found.");
        }

        if (examCentreDto.getCode() != null) {
            if (examCentreDto.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code is empty.");
            }
            ExamCentre daoOtherExamCentre = examCentreDao.findByCode(examCentreDto.getCode());
            if (daoOtherExamCentre != null && !daoOtherExamCentre.getId().equals(daoExamCentre.getId())) {
                throw new InvalidReqPayloadException("Same code already exists");
            }
            daoExamCentre.setCode(examCentreDto.getCode());
            daoAppUser.setUserId(daoExamCentre.getCode()); // also change userId
        }
        if (examCentreDto.getName() != null) {
            if (examCentreDto.getName().isBlank()) {
                throw new InvalidReqPayloadException("Name is empty.");
            }
            daoExamCentre.setName(examCentreDto.getName());
            daoAppUser.setName(daoExamCentre.getName());
        }
        if (examCentreDto.getRegion() != null) {
            Region daoRegion = regionDao.findById(examCentreDto.getRegion().getId());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreDto.getRegion().getId() + " not found");
            }
            daoExamCentre.setRegion(daoRegion);
        }
        appUserDao.save(daoAppUser);
        return examCentreDao.save(daoExamCentre);
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
//                    int size = examDateDao.findByExamCentre(examCentre).size();
                    // TODO; check on this.
                    int size = 1;
                    if (size == 0) {
                        size = 1;
                    }
                    int totalFileCount = (int) (size * fileTypeDao.count() * slotDao.count());

                    int uploadedFileCount = examFileDao.findByExamCentre(examCentre).size();
                    return new ExamCentreResDto(examCentre.getId(), examCentre.getCode(), examCentre.getName(), examCentre.getRegion().getName(), totalFileCount, uploadedFileCount);
                }).toList();
    }

}
