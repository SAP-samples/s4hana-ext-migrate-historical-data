package com.sap.cloud.s4hana.migratehistoricaldata.model;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.BatchFetchType.JOIN;

import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.BatchFetch;

import com.sap.cloud.s4hana.migratehistoricaldata.security.SecurityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Cacheable
@EntityListeners(SecurityListener.class)
public class SalesOrderItem implements Identified<Long> {
	
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	
	@NotNull
	@Column(nullable = false)
	private Long salesOrderItem;
	
	@NotNull
	@Column
	private String material;
	
	@Column(scale = 3, precision = 9)
	private BigDecimal requestedQuantity;
	
	@Column
	private String requestedQuantityUnit;
	
	@Column
	private String itemCategory;
	
	@Column(scale = 2, precision = 9)
	private BigDecimal netValue;
	
	@Column
	private String documentCurrency;
	
	@Column
	private String plant;
	
	@Column
	private String shipToParty;
	
	@Column
	private String incotermsVersion;
	
	@Column
	private String incotermsPart1;
	
	@Column
	private String incotermsLocation1;
	
	@Column
	private String incotermsPart2;
	
	@Column
	private String incotermsLocation2;
	
	@Column
	private String termsOfPaymentKey;
	
	@Column
	private String materialDescription;
	
	@ManyToOne(fetch = EAGER)
	@BatchFetch(JOIN)
	@JoinColumn
	private SalesOrder salesOrder;

}
