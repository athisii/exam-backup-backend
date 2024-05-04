package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.entity.AuditModel;
import com.cdac.exambackup.service.BaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractBaseService<E extends AuditModel, K> implements BaseService<E, K> {
    final BaseDao<E, K> baseDao;

    protected AbstractBaseService(BaseDao<E, K> baseDao) {
        this.baseDao = baseDao;
    }

    @Transactional
    public void saveOrUpdate(E entity) {
        this.baseDao.save(entity);
    }

    @Transactional
    public E save(E entity) {
        return this.baseDao.save(entity);
    }

    @Transactional
    public List<E> save(Collection<E> entities) {
        return this.baseDao.save(entities);
    }

    @Transactional(readOnly = true)
    public List<E> getAll() {
        ListRequest listRequest = new ListRequest();
        listRequest.setFilters(new HashMap<>());
        return this.baseDao.list(listRequest);
    }

    @Transactional(readOnly = true)
    public E getById(K id) {
        E e = this.baseDao.findById(id);
        if (Objects.isNull(e)) {
            throw new EntityNotFoundException(this.baseDao.getEntityClass().getSimpleName() + " with id: " + id + " not found.");
        }
        return e;
    }

    @Transactional
    public void remove(E entity) {
        this.baseDao.delete(entity);
    }


    @Transactional
    public void activateById(K id) {
        this.baseDao.activateById(id);
    }

    @Transactional
    public void activate(E entity) {
        this.baseDao.activate(entity);
    }

    @Transactional
    public void deactivate(E entity) {
        this.baseDao.deactivate(entity);
    }

    @Transactional
    public void deactivateById(K id) {
        this.baseDao.deactivateById(id);
    }

    @Transactional
    public void softDelete(E entity) {
        this.baseDao.softDelete(entity);
    }

    @Transactional
    public void softDeleteById(K id) {
        this.baseDao.softDeleteById(id);
    }

    @Transactional(readOnly = true)
    public Long count(ListRequest listRequest) {
        return this.baseDao.count(listRequest);
    }

    @Transactional(readOnly = true)
    public List<E> getAllByIdIn(List<K> list) {
        return this.baseDao.findAllById(list);
    }

    @Transactional
    public void refresh(E model) {
        this.baseDao.refresh(model);
    }

    @Transactional(readOnly = true)
    public List<E> list(ListRequest listRequest) {
        return this.baseDao.list(listRequest);
    }

    @Transactional(readOnly = true)
    public Long count() {
        return this.baseDao.count();
    }
}
