package com.sap.cloud.s4hana.migratehistoricaldata.model;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.BatchFetchType.JOIN;

import java.math.BigDecimal;

import org.eclipse.persistence.annotations.BatchFetch;

import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmIgnore;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Cacheable
//@EntityListeners(SecurityListener.class)
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
    
    
    @EdmIgnore
    @Column(name = "SALESORDER_ID", insertable = false, updatable = false)
    private Long salesOrderID;

    @ManyToOne(fetch = EAGER)
    @BatchFetch(JOIN)
    @JoinColumn(name = "SALESORDER_ID")
    private SalesOrder salesOrder;
    
    

}
