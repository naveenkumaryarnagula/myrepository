package com.ewig.user.Document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "follow")
public class Follow {
    private String celebId;
    private String userId;
    private Boolean isFollow;
}
