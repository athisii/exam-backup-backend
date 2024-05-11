package com.cdac.exambackup;

import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.service.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        examSlotCodeNameMap.put(1, "SLOT_1");
        examSlotCodeNameMap.put(2, "SLOT_2");
        examSlotCodeNameMap.put(3, "SLOT_3");
        examSlotCodeNameMap.put(4, "SLOT_4");

        fileTypeCodeNameMap = new TreeMap<>();
        fileTypeCodeNameMap.put(1, "PXE_LOG");
        fileTypeCodeNameMap.put(2, "PRIMARY_SERVER_LOG");
        fileTypeCodeNameMap.put(3, "SECONDARY_SERVER_LOG");
        fileTypeCodeNameMap.put(4, "RESPONSE_SHEET");
        fileTypeCodeNameMap.put(5, "ATTENDANCE_SHEET");
        fileTypeCodeNameMap.put(6, "BIOMETRIC_DATA");

        examCentreCodeNameMap = new TreeMap<>();
        examCentreCodeNameMap.put(1, "EC1");
        examCentreCodeNameMap.put(2, "EC2");
        examCentreCodeNameMap.put(3, "EC3");
        examCentreCodeNameMap.put(4, "EC4");
    }

    @Override
    public void run(String... args) {

        if (searchConfigService.count() == 0L) {
            List<SearchConfig> searchConfigs = new ArrayList<>();
            searchConfigs.add(new SearchConfig("Role", "name,code"));
            searchConfigs.add(new SearchConfig("Region", "name,code"));
            searchConfigs.add(new SearchConfig("ExamSlot", "name,code"));
            searchConfigs.add(new SearchConfig("FileType", "name,code"));
            searchConfigs.add(new SearchConfig("ExamCentre", "name,code"));
            searchConfigs.add(new SearchConfig("User", "userId,name,email,mobileNumber"));
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
            List<ExamCentre> examCentres = new ArrayList<>();
            examCentreCodeNameMap.forEach((code, name) -> {
                var examCentre = new ExamCentre();
                examCentre.setCode(code + "");
                examCentre.setName(name);
                examCentres.add(examCentre);
                Region region = regionService.getById(Long.parseLong(code + ""));
                examCentre.setRegion(region);
                User user = new User();
                user.setUserId(code + "");
                user.setName(name);
                //TODO: password is plain text for now
                user.setPassword("password");
                user.setEmail(name + "@email.com");
                user.setRole(roleService.getById(Integer.toUnsignedLong(3))); // user `USER`
                user.setMobileNumber("+91813281764" + code);
                user.setExamCentre(examCentre);
                examCentre.setUser(user);
            });
            examCentreService.save(examCentres);
        }
    }
}
