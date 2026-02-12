package cn.kmdckj.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 树形结构工具类
 */
public class TreeUtil {

    /**
     * 构建树形结构
     *
     * @param list 原始列表
     * @param rootId 根节点ID
     * @param getId 获取ID的函数
     * @param getParentId 获取父ID的函数
     * @param setChildren 设置子节点的函数
     * @param <T> 节点类型
     * @param <ID> ID类型
     * @return 树形结构列表
     */
    public static <T, ID> List<T> buildTree(List<T> list, ID rootId,
                                            Function<T, ID> getId,
                                            Function<T, ID> getParentId,
                                            java.util.function.BiConsumer<T, List<T>> setChildren) {
        List<T> result = new ArrayList<>();

        if (list == null || list.isEmpty()) {
            return result;
        }

        // 查找根节点
        for (T item : list) {
            if (Objects.equals(getParentId.apply(item), rootId)) {
                result.add(item);
                // 递归设置子节点
                setChildren.accept(item, findChildren(item, list, getId, getParentId, setChildren));
            }
        }

        return result;
    }

    /**
     * 查找子节点
     */
    private static <T, ID> List<T> findChildren(T parent, List<T> list,
                                                Function<T, ID> getId,
                                                Function<T, ID> getParentId,
                                                java.util.function.BiConsumer<T, List<T>> setChildren) {
        List<T> children = new ArrayList<>();
        ID parentId = getId.apply(parent);

        for (T item : list) {
            if (Objects.equals(getParentId.apply(item), parentId)) {
                children.add(item);
                // 递归设置子节点
                setChildren.accept(item, findChildren(item, list, getId, getParentId, setChildren));
            }
        }

        return children;
    }

    /**
     * 获取所有子节点ID（包含自己）
     *
     * @param list 原始列表
     * @param parentId 父节点ID
     * @param getId 获取ID的函数
     * @param getParentId 获取父ID的函数
     * @param <T> 节点类型
     * @param <ID> ID类型
     * @return 所有子节点ID列表
     */
    public static <T, ID> List<ID> getAllChildIds(List<T> list, ID parentId,
                                                  Function<T, ID> getId,
                                                  Function<T, ID> getParentId) {
        List<ID> result = new ArrayList<>();
        result.add(parentId);

        if (list == null || list.isEmpty()) {
            return result;
        }

        findAllChildIds(list, parentId, getId, getParentId, result);
        return result;
    }

    /**
     * 递归查找所有子节点ID
     */
    private static <T, ID> void findAllChildIds(List<T> list, ID parentId,
                                                Function<T, ID> getId,
                                                Function<T, ID> getParentId,
                                                List<ID> result) {
        for (T item : list) {
            if (Objects.equals(getParentId.apply(item), parentId)) {
                ID id = getId.apply(item);
                result.add(id);
                findAllChildIds(list, id, getId, getParentId, result);
            }
        }
    }
}