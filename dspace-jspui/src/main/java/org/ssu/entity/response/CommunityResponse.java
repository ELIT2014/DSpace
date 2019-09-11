package org.ssu.entity.response;

import org.dspace.content.Collection;
import org.dspace.content.Community;

import java.util.List;
import java.util.Map;

public class CommunityResponse {
    private List<Community> communities;
    private Map<String, List<Collection>> colMap;
    private Map<String, List<Community>> commMap;

    private CommunityResponse(Builder builder) {
        communities = builder.communities;
        colMap = builder.colMap;
        commMap = builder.commMap;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public Map<String, List<Collection>> getColMap() {
        return colMap;
    }

    public Map<String, List<Community>> getCommMap() {
        return commMap;
    }


    public static final class Builder {
        private List<Community> communities;
        private Map<String, List<Collection>> colMap;
        private Map<String, List<Community>> commMap;

        public Builder() {
        }

        public Builder(CommunityResponse copy) {
            this.communities = copy.getCommunities();
            this.colMap = copy.getColMap();
            this.commMap = copy.getCommMap();
        }

        public Builder withCommunities(List<Community> communities) {
            this.communities = communities;
            return this;
        }

        public Builder withColMap(Map<String, List<Collection>> colMap) {
            this.colMap = colMap;
            return this;
        }

        public Builder withCommMap(Map<String, List<Community>> commMap) {
            this.commMap = commMap;
            return this;
        }

        public CommunityResponse build() {
            return new CommunityResponse(this);
        }
    }
}
