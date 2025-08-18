package ibm.newgen.mobility.remoteOperation.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Scope(value = "trip_scope")
@Collection(value = "trip_lat_longs_collection")
@Document(expiry = 7200)
public class TripLatLongs {
    
    @Id
	@GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String vinTripId;

    private List<LatLongs> latLongs;
}
