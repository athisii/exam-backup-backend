package com.cdac.exambackup;

import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.service.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class Bootstrap implements CommandLineRunner {
    static final Map<Integer, String> roleCodeNameMap;
    static final Map<Integer, String> regionCodeNameMap;
    static final Map<Integer, String> examSlotCodeNameMap;
    static final Map<Integer, String> fileTypeCodeNameMap;
    static final Map<Integer, String> examCentreCodeNameMap;

    @Autowired
    SearchConfigService searchConfigService;

    @Autowired
    RoleService roleService;

    @Autowired
    RegionService regionService;

    @Autowired
    ExamSlotService examSlotService;

    @Autowired
    FileTypeService fileTypeService;

    @Autowired
    ExamCentreService examCentreService;

    @Autowired
    ExamDateService examDateService;

    @Autowired
    AppUserService appUserService;

    @Autowired
    PasswordEncoder passwordEncoder;


    static {
        roleCodeNameMap = new TreeMap<>();
        roleCodeNameMap.put(1, "ADMIN");
        roleCodeNameMap.put(2, "STAFF");
        roleCodeNameMap.put(3, "USER");
        roleCodeNameMap.put(4, "OTHER");

        regionCodeNameMap = new TreeMap<>();
        regionCodeNameMap.put(1, "NORTH");
        regionCodeNameMap.put(2, "EAST");
        regionCodeNameMap.put(3, "SOUTH");
        regionCodeNameMap.put(4, "WEST");

        examSlotCodeNameMap = new TreeMap<>();
        examSlotCodeNameMap.put(1, "SLOT 1");
        examSlotCodeNameMap.put(2, "SLOT 2");
        examSlotCodeNameMap.put(3, "SLOT 3");
        examSlotCodeNameMap.put(4, "SLOT 4");

        fileTypeCodeNameMap = new TreeMap<>();
        fileTypeCodeNameMap.put(1, "PXE LOG");
        fileTypeCodeNameMap.put(2, "PRIMARY SERVER LOG");
        fileTypeCodeNameMap.put(3, "SECONDARY SERVER LOG");
        fileTypeCodeNameMap.put(4, "RESPONSE SHEET");
        fileTypeCodeNameMap.put(5, "ATTENDANCE SHEET");
        fileTypeCodeNameMap.put(6, "BIOMETRIC DATA");

        examCentreCodeNameMap = new TreeMap<>();
        examCentreCodeNameMap.put(101, "Exam Centre 1");
        examCentreCodeNameMap.put(102, "Exam Centre 2");
        examCentreCodeNameMap.put(103, "Exam Centre 3");
        examCentreCodeNameMap.put(104, "Exam Centre 4");
    }

    @Override
    public void run(String... args) {


        if (searchConfigService.count() == 0L) {
            List<SearchConfig> searchConfigs = new ArrayList<>();
            searchConfigs.add(new SearchConfig("Role", "name,code"));
            searchConfigs.add(new SearchConfig("Region", "name,code"));
            searchConfigs.add(new SearchConfig("ExamSlot", "name,code"));
            searchConfigs.add(new SearchConfig("FileType", "name,code"));
            searchConfigs.add(new SearchConfig("ExamFile", "contentType,userUploadedFilename"));
            searchConfigs.add(new SearchConfig("ExamCentre", "name,code"));
            searchConfigs.add(new SearchConfig("AppUser", "userId,name,email,mobileNumber"));
            searchConfigs.add(new SearchConfig("ExamDate", "examDate"));
            searchConfigService.dump(searchConfigs);
        }

        if (roleService.count() == 0L) {
            List<Role> roles = new ArrayList<>();
            roleCodeNameMap.forEach((code, name) -> {
                Role role = new Role();
                role.setCode(code);
                role.setName(name);
                roles.add(role);
            });
            roleService.save(roles);
        }

        if (appUserService.count() == 0L) {
            var appUser = new AppUser();
            appUser.setUserId("admin");
            appUser.setPassword(passwordEncoder.encode("admin"));
            appUser.setEmail("admin@cdac.in");
            appUser.setRole(roleService.getById(Integer.toUnsignedLong(1))); // user `ADMIN`
            appUserService.save(appUser);
        }


        if (regionService.count() == 0L) {
            List<Region> regions = new ArrayList<>();
            regionCodeNameMap.forEach((code, name) -> {
                Region region = new Region();
                region.setCode(code);
                region.setName(name);
                regions.add(region);
            });
            regionService.save(regions);
        }

        if (examSlotService.count() == 0L) {
            List<ExamSlot> examSlots = new ArrayList<>();
            examSlotCodeNameMap.forEach((code, name) -> {
                var examSlot = new ExamSlot();
                examSlot.setCode(code);
                examSlot.setName(name);
                examSlots.add(examSlot);
            });
            examSlotService.save(examSlots);
        }

        if (fileTypeService.count() == 0L) {
            List<FileType> fileTypes = new ArrayList<>();
            fileTypeCodeNameMap.forEach((code, name) -> {
                var fileType = new FileType();
                fileType.setCode(code);
                fileType.setName(name);
                fileTypes.add(fileType);
            });
            fileTypeService.save(fileTypes);
        }

        if (examCentreService.count() == 0L) {
            examCentreCodeNameMap.forEach((code, name) -> {
                var examCentre = new ExamCentre();
                examCentre.setCode(code + "");
                examCentre.setName(name);
                Region region = regionService.getById(Long.parseLong(code - 100 + ""));
                examCentre.setRegion(region);
                examCentreService.save(examCentre);
            });

            // dummy exam centre for pagination
            Region region = regionService.getById(Long.parseLong("1"));
            IntStream.range(105, 131).forEach(code -> {
                var examCentre = new ExamCentre();
                examCentre.setCode(code + "");
                examCentre.setName("Exam Centre, CDAC Chennai Tidel Park");
                examCentre.setRegion(region);
                examCentreService.save(examCentre);
            });
        }

        if (examDateService.count() == 0L) {
            List<ExamDate> examDates = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                ExamDate examDate = new ExamDate();
                examDate.setExamCentre(examCentreService.getById(1L));
                examDate.setExamSlots(LongStream.range(1, 5).mapToObj(number -> examSlotService.getById(number)).collect(Collectors.toSet()));
                examDate.setExamDate(LocalDateTime.now());
                examDates.add(examDate);
            }
            examDateService.save(examDates);
        }
    }
}
