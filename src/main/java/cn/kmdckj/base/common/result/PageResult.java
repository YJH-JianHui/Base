package cn.kmdckj.base.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回结果类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
    }

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public PageResult(Long total, List<T> records, Long pageNum, Long pageSize) {
        this.total = total;
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = calculatePages(total, pageSize);
    }

    /**
     * 创建分页结果（简单）
     */
    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, records);
    }

    /**
     * 创建分页结果（完整）
     */
    public static <T> PageResult<T> of(Long total, List<T> records, Long pageNum, Long pageSize) {
        return new PageResult<>(total, records, pageNum, pageSize);
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, Collections.emptyList(), 1L, 10L);
    }

    /**
     * 创建空分页结果（指定分页参数）
     */
    public static <T> PageResult<T> empty(Long pageNum, Long pageSize) {
        return new PageResult<>(0L, Collections.emptyList(), pageNum, pageSize);
    }

    /**
     * 计算总页数
     */
    private Long calculatePages(Long total, Long pageSize) {
        if (total == null || total == 0 || pageSize == null || pageSize == 0) {
            return 0L;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return this.pageNum != null && this.pages != null && this.pageNum < this.pages;
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return this.pageNum != null && this.pageNum > 1;
    }
}
