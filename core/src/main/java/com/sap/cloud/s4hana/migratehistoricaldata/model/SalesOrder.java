package com.sap.cloud.s4hana.migratehistoricaldata.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.BatchFetchType.JOIN;

import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.BatchFetch;

import com.sap.cloud.s4hana.migratehistoricaldata.security.SecurityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"items", "successors"})
@Cacheable
@EntityListeners(SecurityListener.class)
public class SalesOrder implements Identified<Long> {
	
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	
	@NotNull
	@Column(nullable = false, unique = true)
	private String legacySalesDocument;
	
	@Column
	private String salesOrganization;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Calendar salesOrderDate;
	
	@NotNull
	@Column(nullable = false)
	private String salesDocumentType;
	
	@Column
	private String documentCurrency;
	
	@Column(scale = 2, precision = 9)
	private BigDecimal netValue;
	
	@Column
	private String soldToParty;
	
	@Column
	private String shipToParty;
	
	@OneToMany(fetch = EAGER, mappedBy = "salesOrder", cascade = ALL, orphanRemoval = true)
	@BatchFetch(JOIN)
	private Set<SalesOrderItem> items = new HashSet<>();
	
	@ManyToMany(fetch = EAGER, mappedBy = "salesOrders")
	@BatchFetch(JOIN)
	private Set<GenericDocument> successors = new HashSet<>();
	
}
