
import java.util.Arrays;
import java.util.List;

public DB2GranteeCache(DB2AuthIDType authIdType)
{
    // Validate the authIdType input
    List<DB2AuthIDType> validAuthIDTypes = Arrays.asList(DB2AuthIDType.G, DB2AuthIDType.U);
    if (!validAuthIDTypes.contains(authIdType)) {
        throw new IllegalArgumentException("Invalid authIdType provided.");
    }
    
    this.authIdType = authIdType;
    this.authIdTypeName = authIdType.name();
}
