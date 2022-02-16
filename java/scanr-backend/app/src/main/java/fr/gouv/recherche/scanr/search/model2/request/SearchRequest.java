/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
 
package fr.gouv.recherche.scanr.search.model2.request;

import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.util.TextFiltering;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.springframework.data.annotation.Transient;

import java.util.*;

/** used in used in (advanced) /x/search */
@ApiModel("v2.SearchRequest") // for consistency
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {
    public static final int PAGE_SIZE = 20;
    private static final int MAX_PAGE = 50;
	public static final String DEFAULT_LANG = ApiConstants.DEFAULT_LANGUAGE;

	public enum AggregationSortType {
		COUNT,
		KEY
	}
    public enum SortDirection {
        ASC(true, "asc"),
        DESC(false, "desc");

		private boolean booleanValue;
		private String value;

		SortDirection(boolean booleanValue, String value) {
			this.booleanValue = booleanValue;
			this.value = value;
		}

		public boolean toBool() {
			return booleanValue;
		}
		public String getValue() {
			return value;
		}
	}

    private String query;
    
    private int page = 0;
    private int pageSize = PAGE_SIZE;
    @ApiModelProperty("Language ex. \"fr\", before v2 was not used")
    private String lang = DEFAULT_LANG;

    /** Use LinkedHashMap to keep sort order */
    @ApiModelProperty("For each field or \"score\", sort order to apply, in the specified order")
    public LinkedHashMap<String,SortDirection> sort;
    public SortDirection sortMapValueTypeDummy;

    @ApiModelProperty("Fields where to look up the query in")
    private List<String> searchFields = new ArrayList<>();

    @ApiModelProperty("Map of field names to (multi value, date, long, geo grid) search filters. Field names can be dotted to specify subfields.")
    @JsonDeserialize(contentUsing=SearchFilterDeserializer.class)
    private Map<String,SearchFilter> filters = new HashMap<>();
    public SearchFilter filtersMapValueTypeDummy;
	public MultiValueSearchFilter filtersMapValueTypeMultiValueDummy;
	public GeoGridFilter filtersMapValueTypeGeoGridDummy;
	public LongRangeFilter filtersMapValueTypeLongRangeDummy;
	public DateRangeFilter filtersMapValueTypeDateRangeDummy;
    
    // result parameters :
    @ApiModelProperty("Fields to be included in result")
    private List<String> sourceFields = new ArrayList<>();

    @ApiModelProperty("Aggregations whose buckets or bins are to be computed and returned")
    private Map<String,Aggregation> aggregations = new HashMap<>();
    public Aggregation aggregationsMapValueTypeDummy;
	
    public String getQuery() {
        return TextFiltering.filterQuery(query);
    }

    public int getPage() {
        return Math.min(page, MAX_PAGE);
    }

    public int getPageSize() {
        return pageSize;
    }

    @JsonIgnore
    public int getFrom() {
        return (getPage() - 1) * getPageSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRequest that = (SearchRequest) o;
        return page == that.page &&
                pageSize == that.pageSize &&
                Objects.equals(query, that.query) &&
                Objects.equals(lang, that.lang) &&
                sort == that.sort &&
                Objects.equals(searchFields, that.searchFields) &&
                Objects.equals(filters, that.filters) &&
                Objects.equals(sourceFields, that.sourceFields) &&
                Objects.equals(aggregations, that.aggregations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, page, pageSize, lang, sort, searchFields, filters, searchFields, aggregations);
    }

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public LinkedHashMap<String, SortDirection> getSort() {
		return sort;
	}

	public void setSort(LinkedHashMap<String, SortDirection> sort) {
		this.sort = sort;
	}

	public List<String> getSearchFields() {
		return searchFields;
	}

	public void setSearchFields(List<String> searchFields) {
		this.searchFields = searchFields;
	}

	public Map<String, SearchFilter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, SearchFilter> filters) {
		this.filters = filters;
	}

	public List<String> getSourceFields() {
		return sourceFields;
	}

	public void setSourceFields(List<String> sourceFields) {
		this.sourceFields = sourceFields;
	}

	public Map<String, Aggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(Map<String, Aggregation> aggregations) {
		this.aggregations = aggregations;
	}

	public static int getMaxPage() {
		return MAX_PAGE;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
