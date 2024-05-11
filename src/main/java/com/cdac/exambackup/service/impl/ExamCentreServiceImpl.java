package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.entity.User;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.ExamCentreService;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    UserDao userDao;

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
                throw new GenericException("Both 'code' and 'name' cannot be null or empty. Please provide either one");
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

            User user = new User();
            user.setName(examCentreDto.getName());
            user.setUserId(examCentreDto.getCode());

            putEmailAndMobileNumberIfValid(examCentreDto, user);

            // TODO: need to encrypt with BCryptEncoder
            // will do after Spring Security added.
            user.setPassword(examCentreDto.getCode()); // need to used BCryptEncoder

            Role daoRole = roleDao.findByName("USER"); // default role
            if (daoRole == null) {
                throw new EntityNotFoundException("Role with name: 'USER' not found");
            }
            user.setRole(daoRole);

            // now remove the unnecessary fields if present or create new object.
            ExamCentre examCentre = new ExamCentre();
            examCentre.setCode(examCentreDto.getCode());
            examCentre.setName(examCentreDto.getName().trim());
            examCentre.setRegion(daoRegion);

            user.setExamCentre(examCentre);
            examCentre.setUser(user); // user will be automatically saved when ExamCentre is saved due to rel-mapping.
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
        User daoUser = userDao.findByUserId(oldExamCode);
        if (daoUser == null) {
            throw new EntityNotFoundException("User with userId: " + oldExamCode + " not found.");
        }

        putEmailAndMobileNumberIfValid(examCentreDto, daoUser);

        if (examCentreDto.getCode() != null) {
            if (examCentreDto.getCode().isBlank()) {
                throw new GenericException("code is empty.");
            }
            ExamCentre daoOtherExamCentre = examCentreDao.findByCode(examCentreDto.getCode());
            if (daoOtherExamCentre != null && !daoOtherExamCentre.getId().equals(daoExamCentre.getId())) {
                throw new GenericException("Same code already exists");
            }
            daoExamCentre.setCode(examCentreDto.getCode());
            daoUser.setUserId(daoExamCentre.getCode()); // also change userId
        }
        if (examCentreDto.getName() != null) {
            if (examCentreDto.getName().isBlank()) {
                throw new GenericException("Name is empty.");
            }
            daoExamCentre.setName(examCentreDto.getName());
            daoUser.setName(daoExamCentre.getName());
        }
        if (examCentreDto.getRegion() != null) {
            Region daoRegion = regionDao.findById(examCentreDto.getRegion().getId());
            if (daoRegion == null) {
                throw new EntityNotFoundException("Region with id: " + examCentreDto.getRegion().getId() + " not found");
            }
            daoExamCentre.setRegion(daoRegion);
        }
        userDao.save(daoUser);
        return examCentreDao.save(daoExamCentre);
    }

    private void putEmailAndMobileNumberIfValid(ExamCentre examCentreDto, User user) {
        if (examCentreDto.getUser() != null) {
            if (examCentreDto.getUser().getMobileNumber() != null) {
                if (!Util.validateMobileNumber(examCentreDto.getUser().getMobileNumber())) {
                    throw new GenericException("Malformed mobile number.");
                }
                user.setMobileNumber(examCentreDto.getUser().getMobileNumber());
            }
            if (examCentreDto.getUser().getEmail() != null) {
                if (!Util.validateEmail(examCentreDto.getUser().getEmail())) {
                    throw new GenericException("Malformed email address.");
                }
                user.setEmail(examCentreDto.getUser().getEmail());
            }
        }
    }
}
