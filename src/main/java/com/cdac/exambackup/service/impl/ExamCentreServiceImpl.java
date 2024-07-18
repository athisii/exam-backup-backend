package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.ExamCentreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                throw new GenericException("Both 'code' and 'name' cannot be null or empty.");
            }
            ExamCentre daoExamCentre = examCentreDao.findByCode(examCentreDto.getCode());
            if (daoExamCentre != null) {
                throw new GenericException("Same 'code' already exists");
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
                        2. userId as examCentreCode
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
            1. if examCentreCode is updated then userId is also to be updated
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
            throw new GenericException("Both 'code' and 'name' cannot be null");
        }

        // if both values are invalid, one should be valid
        if ((examCentreDto.getCode() != null && examCentreDto.getCode().isBlank()) && (examCentreDto.getName() != null && examCentreDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be  empty");
        }
        // examCentreCode is change, so userId & name must also be changed
        // good catch
        AppUser daoAppUser = appUserDao.findByUserId(oldExamCode);
        if (daoAppUser == null) {
            throw new EntityNotFoundException("User with userId: " + oldExamCode + " not found.");
        }

        if (examCentreDto.getCode() != null) {
            if (examCentreDto.getCode().isBlank()) {
                throw new GenericException("code is empty.");
            }
            ExamCentre daoOtherExamCentre = examCentreDao.findByCode(examCentreDto.getCode());
            if (daoOtherExamCentre != null && !daoOtherExamCentre.getId().equals(daoExamCentre.getId())) {
                throw new GenericException("Same code already exists");
            }
            daoExamCentre.setCode(examCentreDto.getCode());
            daoAppUser.setUserId(daoExamCentre.getCode()); // also change userId
        }
        if (examCentreDto.getName() != null) {
            if (examCentreDto.getName().isBlank()) {
                throw new GenericException("Name is empty.");
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

    @Override
    public ExamCentre getByCode(String code) {
        ExamCentre daoExamCentre = examCentreDao.findByCode(code);
        if (daoExamCentre == null || daoExamCentre.getDeleted()) {
            throw new GenericException("No exam centre found with code: " + code);
        }
        return daoExamCentre;
    }
}
