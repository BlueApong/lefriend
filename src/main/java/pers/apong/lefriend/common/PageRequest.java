package pers.apong.lefriend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页数据请求
 *
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 当前页数
     */
    private int pageNum = 1;
    /**
     * 每页数量
     */
    private int pageSize = 10;

    private static final long serialVersionUID = 1L;
}
