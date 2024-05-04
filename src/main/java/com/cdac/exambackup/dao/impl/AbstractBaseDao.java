package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.entity.AuditModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Service
public abstract class AbstractBaseDao<E extends AuditModel, ID extends Serializable> implements BaseDao<E, ID> {
    @PersistenceContext
    protected EntityManager entityManager;

    public abstract JpaRepository<E, ID> getRepository();

    public long count() {
        return this.getRepository().count();
    }

    public E findById(ID id) {
        Optional<E> optional = this.getRepository().findById(id);
        return optional.isPresent() && Boolean.TRUE.equals(!optional.get().getDeleted()) ? optional.get() : null;
    }

    public List<E> findAll() {
        ListRequest listRequest = new ListRequest();
        listRequest.setFilters(new HashMap<>());
        return this.list(listRequest);
    }

    public List<E> findAll(Sort sort) {
        return this.getRepository().findAll(sort);
    }

    public Page<E> findAll(Pageable pageable) {
        return this.getRepository().findAll(pageable);
    }

    public List<E> findAllById(Collection<ID> ids) {
        return this.getRepository().findAllById(ids);
    }

    public void flush() {
        this.getRepository().flush();
    }

    @Transactional
    public E save(E entity) {
        return this.getRepository().save(entity);
    }

    public E saveAndFlush(E entity) {
        return this.getRepository().saveAndFlush(entity);
    }

    public List<E> save(Collection<E> entities) {
        return entities != null && !entities.isEmpty() ? this.getRepository().saveAll(entities) : null;
    }

    public void deleteById(ID id) {
        // ignore if not found
        if (id != null) {
            Optional<E> entity = this.getRepository().findById(id);
            entity.ifPresent(this::delete);
        }
    }

    public void delete(E entity) {
        if (entity != null) {
            this.getRepository().delete(entity);
        }
    }

    public void delete(Collection<E> entities) {
        if (entities != null && !entities.isEmpty()) {
            this.getRepository().deleteAll(entities);
        }
    }

    public void deleteAll() {
        this.getRepository().deleteAll();
    }

    public void activateById(ID id) {
        if (id != null) {
            Optional<E> entity = this.getRepository().findById(id);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException(this.getEntityClass().getSimpleName() + " with id: " + id + " not found.");
            }
            entity.ifPresent(this::activate);
        }
    }

    public void activate(E entity) {
        if (entity != null) {
            entity.setActive(true);
            this.getRepository().save(entity);
        }
    }

    public void activate(Collection<E> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(e -> e.setActive(true));
            this.getRepository().saveAll(entities);
        }
    }

    public void deactivateById(ID id) {
        if (id != null) {
            Optional<E> entity = this.getRepository().findById(id);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException(this.getEntityClass().getSimpleName() + " with id: " + id + " not found.");
            }
            entity.ifPresent(this::deactivate);
        }
    }

    public void deactivate(E entity) {
        if (entity != null) {
            entity.setActive(false);
            this.getRepository().save(entity);
        }
    }

    public void deactivate(Collection<E> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(e -> e.setActive(false));
            this.getRepository().saveAll(entities);
        }
    }

    public void deactivateAll() {
        Collection<E> entities = this.getRepository().findAll();
        if (!entities.isEmpty()) {
            entities.forEach(e -> e.setActive(false));
            this.getRepository().saveAll(entities);
        }
    }

    public void softDeleteById(ID id) {
        if (id != null) {
            Optional<E> entity = this.getRepository().findById(id);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException(this.getEntityClass().getSimpleName() + " with id: " + id + " not found.");
            }
            entity.ifPresent(this::softDelete);
        }
    }

    public void softDelete(E entity) {
        if (entity != null) {
            entity.setDeleted(true);
            this.getRepository().save(entity);
        }
    }

    public void softDelete(Collection<E> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(e -> e.setDeleted(true));
            this.getRepository().saveAll(entities);
        }
    }

    public void softDeleteAll() {
        Collection<E> entities = this.getRepository().findAll();
        if (!entities.isEmpty()) {
            entities.forEach(e -> e.setDeleted(true));
            this.getRepository().saveAll(entities);
        }
    }

    public Long count(ListRequest listRequest) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(this.getEntityClass());
        List<Predicate> predicates = this.getPredicates(listRequest, criteriaBuilder, root);
        criteriaQuery.select(criteriaBuilder.count(root)).where(predicates.toArray(new Predicate[0]));
        return this.entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List<E> list(ListRequest listRequest) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(this.getEntityClass());
        Root<E> root = criteriaQuery.from(this.getEntityClass());
        List<Predicate> conditions = this.getPredicates(listRequest, criteriaBuilder, root);
        return this.createQuery(listRequest, criteriaQuery, criteriaBuilder, root, conditions);
    }

    protected List<Predicate> getPredicates(ListRequest listRequest, CriteriaBuilder criteriaBuilder, Root<E> root) {
        List<Predicate> conditions = new ArrayList<>();
        if (Objects.nonNull(listRequest.getFilters())) {
            this.getFilterPredicates(listRequest, root, conditions);
        }
        this.getSearchPredicates(listRequest, criteriaBuilder, root, conditions);
        conditions.add(criteriaBuilder.equal(root.get("deleted"), false));
        return conditions;
    }

    public void getFilterPredicates(ListRequest listRequest, Root<E> root, List<Predicate> conditions) {
        // column name  = possible values of a row/tuple for lhs column
        // numberOfCore = 6, 7, 8
        // company = amd, intel
        listRequest.getFilters().forEach((k, v) -> {
            try {
                conditions.add(root.get(k).in(v));
            } catch (RuntimeException ex) {
                // ignore for unknown attribute name
            }
        });
    }

    protected void getSearchPredicates(ListRequest listRequest, CriteriaBuilder criteriaBuilder, Root<E> root, List<Predicate> conditions) {
        List<String> searchableColumns = Arrays.asList(this.findAllSearchableColumns());
        List<Predicate> searchPredicates = new ArrayList<>();
        if (Objects.nonNull(listRequest.getSearchTag())) {
            searchableColumns.forEach(searchColumn -> {
                // only search columns which are mentioned in search_config table for the entity.
                try {
                    // only for String.
                    Expression<String> lowerCaseExpression = criteriaBuilder.lower(root.get(searchColumn));
                    searchPredicates.add(criteriaBuilder.like(lowerCaseExpression, "%" + listRequest.getSearchTag().toLowerCase() + "%"));
                } catch (RuntimeException ignored) {
                    // ignore for unknown attribute name
                }
            });
            conditions.add(criteriaBuilder.or(searchPredicates.toArray(Predicate[]::new)));
        }
    }

    protected List<E> createQuery(ListRequest listRequest, CriteriaQuery<E> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<E> root, List<Predicate> predicates) {
        Order order = criteriaBuilder.asc(root.get("id"));
        if (Objects.nonNull(listRequest.getSortBy())) {
            // client might send name not matching the table column names.
            try {
                order = criteriaBuilder.asc(root.get(listRequest.getSortBy()));
            } catch (RuntimeException ignored) {
                // sort by id (default)
            }
            if (Objects.nonNull(listRequest.getSortOrder()) && listRequest.getSortOrder().equalsIgnoreCase("desc")) {
                order = criteriaBuilder.desc(root.get(listRequest.getSortBy()));
            }
        }
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0])).orderBy(order);

        if (listRequest.getStart() < 0) {
            listRequest.setStart(0);
        }
        if (listRequest.getLength() < 0) {
            listRequest.setLength(100);
        }
        return this.entityManager.createQuery(criteriaQuery).setFirstResult(listRequest.getStart()).setMaxResults(listRequest.getLength()).getResultList();
    }

    protected String[] findAllSearchableColumns() {
        String entityName = this.getEntityClass().getSimpleName();
        Query query = this.entityManager.createQuery("SELECT searchableColumns from SearchConfig where entityName='" + entityName + "' or entityName='" + AuditModel.class.getSimpleName() + "'").setFirstResult(0).setMaxResults(1);
        String result = (String) query.getSingleResult();
        return result.split(",");
    }

    @Transactional
    public E updateRecord(E model) {
        return this.getRepository().save(model);
    }

    public void refresh(E model) {
        this.entityManager.refresh(model);
    }
}
