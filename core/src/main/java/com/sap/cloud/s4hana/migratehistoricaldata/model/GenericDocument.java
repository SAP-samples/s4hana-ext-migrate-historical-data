package com.sap.cloud.s4hana.migratehistoricaldata.model;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.BatchFetchType.JOIN;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.BatchFetch;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.DocumentFlowListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.SalesOrderConditionsListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.security.SecurityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude={"successors"})
@Entity
@Cacheable
@EntityListeners(SecurityListener.class)
public class GenericDocument implements Identified<Long> {
	
	public static final String DOCUMENT_TYPE_SALES_ORDER = "C";
	
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	
	@NotNull
	@Column(nullable = false)
	private String document;
	
	@NotNull
	@Column(nullable = false)
	private String type;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Calendar documentDate;

	@ManyToMany(fetch = EAGER)
	@BatchFetch(JOIN)
	private Set<SalesOrder> salesOrders = new HashSet<>();
	
	@ManyToMany(fetch = EAGER)
	@BatchFetch(JOIN)
	private Set<GenericDocument> predecessors = new HashSet<>();
	
	@ManyToMany(fetch = EAGER, mappedBy = "predecessors")
	@BatchFetch(JOIN)
	private Set<GenericDocument> successors = new HashSet<>();
	
	/**
	 * Transient field that is used by
	 * {@link DocumentFlowListenerSupplier#get()} to temporarily store the
	 * predecessor information during the import.
	 * <p>
	 * Another option to do this is to create a data transfer object (DTO) type
	 * similar to {@code ConditionDTO} type of
	 * {@link SalesOrderConditionsListenerSupplier}
	 * 
	 */
	private transient String predecessorType;
	
	/**
	 * Transient field that is used by
	 * {@link DocumentFlowListenerSupplier#get()} to temporarily store the
	 * predecessor information during the import.
	 * <p>
	 * Another option to do this is to create a data transfer object (DTO) type
	 * similar to {@code ConditionDTO} type of
	 * {@link SalesOrderConditionsListenerSupplier}
	 * 
	 */
	private transient String predecessorCode;
	
}
