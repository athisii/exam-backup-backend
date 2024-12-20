package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamCentreReqDto;
import com.cdac.exambackup.dto.ExamCentreResDto;
import com.cdac.exambackup.dto.ExamCentreSlotUpdateReqDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.enums.UploadFilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamCentreService extends BaseService<ExamCentre, Long> {
    PageResDto<List<ExamCentreResDto>> getByQueryOrCodeOrNameOrRegionId(String query, String code, String name, Long regionId, Pageable pageable);

    PageResDto<List<ExamCentreResDto>> searchByQueryAndRegionId(String query, Long regionId, Pageable pageable);

    PageResDto<List<ExamCentreResDto>> getExamCentresOnUploadStatusByPage(String query, UploadFilterType filterType, Long regionId, Pageable pageable);

    PageResDto<List<ExamCentreResDto>> getAllByPage(Pageable pageable);

    ExamCentre save(ExamCentreReqDto examCentreReqDto);

    void bulkUpload(MultipartFile csvFile);

    void updateOnlySlot(ExamCentreSlotUpdateReqDto examCentreSlotUpdateReqDto);

    List<ExamCentreResDto> getAllByRegionIds(List<Long> regionIds);
}
