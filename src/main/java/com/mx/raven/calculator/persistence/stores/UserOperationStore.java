package com.mx.raven.calculator.persistence.stores;

import com.mx.raven.calculator.persistence.entities.UserOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserOperationStore extends JpaRepository<UserOperation, Long>, JpaSpecificationExecutor<UserOperation> {
}
