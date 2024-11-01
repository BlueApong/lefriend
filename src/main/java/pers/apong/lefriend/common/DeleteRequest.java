package pers.apong.lefriend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除数据请求
 *
 */
@Data
public class DeleteRequest implements Serializable {

    private long id;

    private static final long serialVersionUID = 1L;
}
