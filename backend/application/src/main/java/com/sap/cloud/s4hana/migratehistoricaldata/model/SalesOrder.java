package com.sap.cloud.s4hana.migratehistoricaldata.model;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.BatchFetchType.JOIN;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.annotations.BatchFetch;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data // Assuming Lombok is compatible with Java 17
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"items", "successors"})
@Cacheable
//@EntityListeners(SecurityListener.class)
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
    private Calendar salesOrderDate; // Use LocalDate instead of Calendar

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

    
    @OneToMany(mappedBy = "salesOrder", orphanRemoval = true, cascade = CascadeType.ALL)
    @BatchFetch(JOIN)
    private Set<SalesOrderItem> items = new HashSet<>();

    @ManyToMany(fetch = EAGER, mappedBy = "salesOrders")
    @BatchFetch(JOIN)
    private Set<GenericDocument> successors = new HashSet<>();


}
