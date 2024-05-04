package com.cdac.exambackup;

import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.service.RegionService;
import com.cdac.exambackup.service.RoleService;
import com.cdac.exambackup.service.SearchConfigService;
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

    @Autowired
    SearchConfigService searchConfigService;

    @Autowired
    RoleService roleService;

    @Autowired
    RegionService regionService;

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
    }

    @Override
    public void run(String... args) {

        if (searchConfigService.count() < 1) {
            searchConfigService.dump();
        }

        if (roleService.count() < 1) {
            List<Role> roles = new ArrayList<>();
            roleCodeNameMap.forEach((code, name) -> {
                Role role = new Role();
                role.setCode(code);
                role.setName(name);
                roles.add(role);
            });
            roleService.save(roles);
        }

        if (regionService.count() < 1) {
            List<Region> regions = new ArrayList<>();
            regionCodeNameMap.forEach((code, name) -> {
                Region region = new Region();
                region.setCode(code);
                region.setName(name);
                regions.add(region);
            });
            regionService.save(regions);
        }
    }
}
