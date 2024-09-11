package com.cdac.exambackup;

import com.cdac.exambackup.dao.repo.ExamRepository;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.dto.ExamCentreReqDto;
import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.service.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class Bootstrap implements CommandLineRunner {
    static final String COMMON_FIELDS = "name,code";
    static final Map<String, String> roleCodeNameMap;
    static final Map<String, String> regionCodeNameMap;
    static final Map<String, String> slotCodeNameMap;
    static final Map<String, String> fileTypeCodeNameMap;
    static final Map<Integer, String> examCentreCodeNameMap;

    @Autowired
    SearchConfigService searchConfigService;

    @Autowired
    RoleService roleService;

    @Autowired
    RegionService regionService;

    @Autowired
    SlotService slotService;

    @Autowired
    FileTypeService fileTypeService;

    @Autowired
    ExamCentreService examCentreService;

    @Autowired
    ExamDateService examDateService;
    @Autowired
    ExamRepository examRepository;
    @Autowired
    ExamSlotRepository examSlotRepository;

    @Autowired
    AppUserService appUserService;

    @Autowired
    PasswordEncoder passwordEncoder;


    static {
        roleCodeNameMap = new TreeMap<>();
        roleCodeNameMap.put("1", "ADMIN");
        roleCodeNameMap.put("2", "STAFF");
        roleCodeNameMap.put("3", "USER");
        roleCodeNameMap.put("4", "OTHER");

        regionCodeNameMap = new TreeMap<>();
        regionCodeNameMap.put("1", "NORTH");
        regionCodeNameMap.put("2", "EAST");
        regionCodeNameMap.put("3", "SOUTH");
        regionCodeNameMap.put("4", "WEST");

        slotCodeNameMap = new TreeMap<>();
        slotCodeNameMap.put("1", "SLOT 1");
        slotCodeNameMap.put("2", "SLOT 2");
        slotCodeNameMap.put("3", "SLOT 3");
        slotCodeNameMap.put("4", "SLOT 4");

        fileTypeCodeNameMap = new TreeMap<>();
        fileTypeCodeNameMap.put("1", "PXE LOG");
        fileTypeCodeNameMap.put("2", "PRIMARY SERVER LOG");
        fileTypeCodeNameMap.put("3", "SECONDARY SERVER LOG");
        fileTypeCodeNameMap.put("4", "RESPONSE SHEET");
        fileTypeCodeNameMap.put("5", "ATTENDANCE SHEET");
        fileTypeCodeNameMap.put("6", "BIOMETRIC DATA");

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
            searchConfigs.add(new SearchConfig("Role", COMMON_FIELDS));
            searchConfigs.add(new SearchConfig("Region", COMMON_FIELDS));
            searchConfigs.add(new SearchConfig("Slot", COMMON_FIELDS));
            searchConfigs.add(new SearchConfig("FileType", COMMON_FIELDS));
            searchConfigs.add(new SearchConfig("ExamCentre", COMMON_FIELDS));
            searchConfigs.add(new SearchConfig("ExamFile", "contentType,userUploadedFilename"));
            searchConfigs.add(new SearchConfig("AppUser", "userId,name,email,mobileNumber"));
            searchConfigs.add(new SearchConfig("ExamDate", "date"));
            searchConfigs.add(new SearchConfig("Exam", ""));
            searchConfigs.add(new SearchConfig("ExamSlot", ""));
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

        if (slotService.count() == 0L) {
            List<Slot> slots = new ArrayList<>();
            slotCodeNameMap.forEach((code, name) -> {
                var examSlot = new Slot();
                examSlot.setCode(code);
                examSlot.setName(name);
                int startHour = 8 + Integer.parseInt(code);
                LocalTime startTime = LocalTime.of(startHour, 0, 0);
                examSlot.setStartTime(startTime);
                examSlot.setEndTime(startTime.plusHours(1)); // one hour duration
                slots.add(examSlot);
            });
            slotService.save(slots);
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
                var examCentreReqDto = new ExamCentreReqDto(null, code + "", name, regionCodeNameMap.get(code - 100 + ""), "8132817645", code + "@email.com", null);
                examCentreService.save(examCentreReqDto);
            });

            // dummy exam centre for pagination
            IntStream.range(105, 131).forEach(code -> {
                var examCentreReqDto = new ExamCentreReqDto(null, code + "", "Exam Centre " + code + ", CDAC Chennai Tidel Park", regionCodeNameMap.get(code % 4 == 0 ? 1 + "" : (code % 4) + ""), "7005701479", code + "@email.com", null);
                examCentreService.save(examCentreReqDto);
            });
        }

        if (examDateService.count() == 0L) {
            List<ExamDate> examDates = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ExamDate examDate = new ExamDate();
                examDate.setDate(LocalDate.now().plusDays(i));
                examDates.add(examDate);
            }
            examDateService.save(examDates);
        }

        examRepository.saveAll(List.of(
                new Exam(examCentreService.getById(1L), examDateService.getById(1L)),
                new Exam(examCentreService.getById(1L), examDateService.getById(2L)),
                new Exam(examCentreService.getById(2L), examDateService.getById(1L)),
                new Exam(examCentreService.getById(2L), examDateService.getById(2L))));

        examSlotRepository.saveAll(List.of(
                new ExamSlot(examRepository.findById(1L).get(), slotService.getById(1L)),
                new ExamSlot(examRepository.findById(1L).get(), slotService.getById(2L)),
                new ExamSlot(examRepository.findById(3L).get(), slotService.getById(1L)),
                new ExamSlot(examRepository.findById(3L).get(), slotService.getById(2L))
        ));
    }
}
