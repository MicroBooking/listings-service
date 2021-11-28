package entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name="listing")
@NamedQueries(value = {
        @NamedQuery(name="ListingEntity.getAll",
                query="SELECT listing FROM ListingEntity listing"),
        @NamedQuery(name="ListingEntity.getById",
                query="SELECT listing FROM ListingEntity listing where listing.id=:id")
})
public class ListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="type")
    private String type;

    @Column(name="monthly_price")
    private Integer monthlyPrice;

    @Column(name = "created")
    private Instant created;

    @Column(name="owner_id")
    private Integer ownerId;

    @Column(name = "reserved")
    private boolean reserved;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(Integer monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
