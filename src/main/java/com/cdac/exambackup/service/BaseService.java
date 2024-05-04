package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.entity.AuditModel;

import java.util.Collection;
import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

public interface BaseService<E extends AuditModel, K> {
    void saveOrUpdate(E entity);

    List<E> getAll();

    E getById(K id);

    void remove(E entity);

    E save(E entity);

    List<E> save(Collection<E> entities);

    void activateById(K id);

    void activate(E entity);

    void deactivate(E entity);

    void deactivateById(K id);

    void softDelete(E entity);

    void softDeleteById(K id);

    List<E> list(ListRequest listRequest);

    Long count(ListRequest listRequest);

    Long count();

    List<E> getAllByIdIn(List<K> list);

    void refresh(E model);
}
