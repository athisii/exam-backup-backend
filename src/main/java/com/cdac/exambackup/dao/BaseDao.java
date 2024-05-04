package com.cdac.exambackup.dao;

import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.entity.AuditModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

// E entity, K = primary key type
public interface BaseDao<E extends AuditModel, ID> {
    Class<E> getEntityClass();

    E save(E entity);

    List<E> save(Collection<E> entities);

    E saveAndFlush(E entity);

    E findById(ID id);

    List<E> findAll();

    List<E> findAll(Sort sort);

    Page<E> findAll(Pageable pageable);

    List<E> findAllById(Collection<ID> ids);

    void flush();

    long count();

    void deleteById(ID id);

    void delete(E entity);

    void delete(Collection<E> entities);

    void deleteAll();

    void activateById(ID id);

    void activate(E entity);

    void activate(Collection<E> entities);

    void deactivateById(ID id);

    void deactivate(E entity);

    void deactivate(Collection<E> entities);

    void deactivateAll();

    void softDeleteById(ID id);

    void softDelete(E entity);

    void softDelete(Collection<E> entities);

    void softDeleteAll();

    List<E> list(ListRequest listRequest);

    Long count(ListRequest listRequest);

    E updateRecord(E model);

    void refresh(E model);
}
