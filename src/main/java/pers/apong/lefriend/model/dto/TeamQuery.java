package pers.apong.lefriend.model.dto;

import pers.apong.lefriend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 队伍搜索参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * ids
     */
    private List<Long> ids;

    /**
     * 搜索关键词（同时查找名称和描述）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍可见：0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
