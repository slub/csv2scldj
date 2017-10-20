/**
 * Copyright © 2017 SLUB Dresden (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.slubdresden.csv2scldj.schema.finc;

import de.slubdresden.csv2scldj.CSV2SCLDJException;

public enum FincSolrSchema {

	ACCESS_FACET("access_facet", false),
	ALLFIELDS("allfields", true),
	ALLFIELDS_UNSTEMMED("allfields_unstemmed", true),
	AUTHOR("author", true),
	AUTHOR2("author2", true),
	AUTHOR2_FULLER("author2_fuller", true),
	AUTHOR2_ORIG("author2_orig", true),
	AUTHOR2_ROLE("author2_role", true),
	AUTHOR2_VARIANT("author2_variant", true),
	AUTHOR_ADDITIONAL("author_additional", true),
	AUTHOR_BROWSE("author_browse", true),
	AUTHOR_CORP_REF("author_corp_ref", true),
	AUTHOR_CORPORATE("author_corporate", true),
	AUTHOR_CORPORATE2("author_corporate2", true),
	AUTHOR_CORPORATE2_ORIG("author_corporate2_orig", true),
	AUTHOR_CORPORATE2_ROLE("author_corporate2_role", true),
	AUTHOR_CORPORATE_ORIG("author_corporate_orig", true),
	AUTHOR_CORPORATE_ROLE("author_corporate_role", true),
	AUTHOR_FACET("author_facet", true),
	AUTHOR_FULLER("author_fuller", true),
	AUTHOR_ID("author_id", true),
	AUTHOR_ORIG("author_orig", true),
	AUTHOR_REF("author_ref", true),
	AUTHOR_ROLE("author_role", true),
	AUTHOR_SORT("author_sort", null),
	AUTHOR_VARIANT("author_variant", true),
	AUTHORIZED_MODE("authorized_mode", false),
	AUTHORSTR("authorStr", null),
	BARCODE("barcode", true),
	BRANCH_DE105("branch_de105", true),
	BRANCH_DE14("branch_de14", true),
	BRANCH_DE15("branch_de15", true),
	BRANCH_DE520("branch_de520", true),
	BRANCH_DECH1("branch_dech1", true),
	BRANCH_DEZI4("branch_dezi4", true),
	BRANCH_DEZWI2("branch_dezwi2", true),
	BUILDING("building", true),
	CALLNUMBER_FIRST("callnumber-first", null),
	CALLNUMBER_LABEL("callnumber-label", null),
	CALLNUMBER_RAW("callnumber-raw", true),
	CALLNUMBER_SEARCH("callnumber-search", true),
	CALLNUMBER_SORT("callnumber-sort", null),
	CALLNUMBER_SUBJECT("callnumber-subject", null),
	COLLCODE_DE105("collcode_de105", true),
	COLLCODE_DE15("collcode_de15", true),
	COLLCODE_DE520("collcode_de520", true),
	COLLCODE_DECH1("collcode_dech1", true),
	COLLCODE_DEZI4("collcode_dezi4", true),
	COLLCODE_DEZWI2("collcode_dezwi2", true),
	COLLECTION("collection", true),
	CONTAINER_ISSUE("container_issue", null),
	CONTAINER_REFERENCE("container_reference", null),
	CONTAINER_START_PAGE("container_start_page", null),
	CONTAINER_TITLE("container_title", null),
	CONTAINER_VOLUME("container_volume", null),
	CONTENTS("contents", true),
	COPIES_URL("copies_url", true),
	CTRLNUM("ctrlnum", true),
	DATESPAN("dateSpan", true),
	DESCRIPTION("description", null),
	DEWEY_FULL("dewey-full", true),
	DEWEY_HUNDREDS("dewey-hundreds", true),
	DEWEY_ONES("dewey-ones", true),
	DEWEY_RAW("dewey-raw", true),
	DEWEY_SEARCH("dewey-search", true),
	DEWEY_SORT("dewey-sort", null),
	DEWEY_TENS("dewey-tens", true),
	DISSERATION_NOTE("dissertation_note", true),
	EDITION("edition", null),
	ERA("era", true),
	ERA_FACET("era_facet", true),
	FACET_DE_14_BRANCH_COLLCODE_EXCEPTION("facet_de14_branch_collcode_exception", true),
	FILM_HEADING("film_heading", true),
	FINC_CLASS_FACET("finc_class_facet", true),
	FIRST_INDEXED("first_indexed", null),
	FOOTNOTE("footnote", true),
	FORMAT("format", true),
	FORMAT_DE105("format_de105", true),
	FORMAT_DE14("format_de14", true),
	FORMAT_DE15("format_de15", true),
	FORMAT_DE520("format_de520", true),
	FORMAT_DE540("format_de540", true),
	FORMAT_DECH1("format_dech1", true),
	FORMAT_DED117("format_ded117", true),
	FORMAT_DEGLA1("format_degla1", true),
	FORMAT_DEL152("format_del152", true),
	FORMAT_DEL189("format_del189", true),
	FORMAT_DEZI4("format_dezi4", true),
	FORMAT_DEZWI2("format_dezwi2", true),
	FORMAT_NRW("format_nrw", true),
	FULLRECORD("fullrecord", null),
	FULLTEXT("fulltext", null),
	FULLTEXT_UNSTEMMED("fulltext_unstemmed", null),
	GENRE("genre", true),
	GENRE_FACET("genre_facet", true),
	GEOGR_CODE("geogr_code", true),
	GEOGR_CODE_PERSON("geogr_code_person", true),
	GEOGRAPHIC("geographic", true),
	GEOGRAPHIC_FACET("geographic_facet", true),
	HIERARCHY_BROWSE("hierarchy_browse", true),
	HIERARCHY_PARENT_ID("hierarchy_parent_id", true),
	HIERARCHY_PARENT_TITLE("hierarchy_parent_title", true),
	HIERARCHY_SEQUENCE("hierarchy_sequence", true),
	HIERARCHY_TOP_ID("hierarchy_top_id", true),
	HIERARCHY_TOP_TITLE("hierarchy_top_title", true),
	HIERARCHYTYPE("hierarchytype", false),
	ID("id", null),
	ILLUSTRATED("illustrated", false),
	IMPRINT("imprint", false),
	INSTITUTION("institution", true),
	IS_HIERARCHY_ID("is_hierarchy_id", false),
	IS_HIERARCHY_TITLE("is_hierarchy_title", false),
	ISBN("isbn", true),
	ISMN("ismn", true),
	ISSN("issn", true),
	ITEMDATA("itemdata", null),
	LANGUAGE("language", true),
	LAST_INDEXED("last_indexed", null),
	LCCN("lccn", null),
	LOCAL_CLASS_DEL242("local_class_del242", true),
	LOCAL_HEADING_DEL242("local_heading_del242", true),
	LOCAL_HEADING_DEZWI2("local_heading_dezwi2", true),
	LOCAL_HEADING_FACET_DEL152("local_heading_facet_del152", true),
	LOCAL_HEADING_FACET_DEZWI2("local_heading_facet_dezwi2", true),
	LONG_ALT("long_lat", false),
	MARC_ERROR("marc_error", true),
	MEGA_COLLECTION("mega_collection", true),
	MISC_DE105("misc_de105", true),
	MISC_DECH1("misc_dech1", true),
	MISC_DEL152("misc_del152", true),
	MULTIPART_LINK("multipart_link", true),
	MULTIPART_PART("multipart_part", true),
	MULTIPART_SET("multipart_set", false),
	MUSIC_HEADING("music_heading", true),
	MUSIC_HEADING_BROWSE("music_heading_browse", true),
	OCLC_NUM("oclc_num", true),
	PERFORMER_NOTE("performer_note", true),
	PHYSICAL("physical", true),
	PUBLISH_DATE("publishDate", true),
	PUBLISH_DATE_SORT("publishDateSort", null),
	PUBLISH_PLACE("publishPlace", true),
	PUBLISHER("publisher", true),
	PUBLISHERSTR("publisherStr", true),
	PURCHASE("purchase", true),
	RECORD_ID("record_id", false),
	RECORDTYPE("recordtype", null),
	RSN("rsn", true),
	RVK_FACET("rvk_facet", true),
	RVK_LABEL("rvk_label", true),
	RVK_PATH("rvk_path", true),
	SERIES("series", true),
	SERIES2("series2", true),
	SERIES_ORIG("series_orig", true),
	SIGNATUR("signatur", true),
	SOURCE_ID("source_id", false),
	SPELLING("spelling", true),
	SPELLINGSHINGLE("spellingShingle", true),
	THUMBNAIL("thumbnail", null),
	TIMECODE("timecode", true),
	TITLE("title", null),
	TITLE_ALT("title_alt", true),
	TITLE_AUTH("title_auth", null),
	TITLE_FULL("title_full", null),
	TITLE_FULL_UNSTEMMED("title_full_unstemmed", null),
	TITLE_FULLSTR("title_fullStr", null),
	TITLE_IN_HIERARCHY("title_in_hierarchy", true),
	TITLE_NEW("title_new", true),
	TITLE_OLD("title_old", true),
	TITLE_ORIG("title_orig", false),
	TITLE_PART("title_part", null),
	TITLE_SHORT("title_short", null),
	TITLE_SORT("title_sort", null),
	TITLE_SUB("title_sub", null),
	TITLE_UNIFORM("title_uniform", false),
	TOPIC("topic", true),
	TOPIC_BROWSE("topic_browse", true),
	TOPIC_FACET("topic_facet", true),
	TOPIC_ID("topic_id", true),
	TOPIC_REF("topic_ref", true),
	TOPIC_UNSTEMMED("topic_unstemmed", true),
	UDK_FACET_DE105("udk_facet_de105", true),
	UDK_RAW_DE105("udk_raw_de105", true),
	UDK_RAW_DEL189("udk_raw_del189", true),
	URL("url", true),
	URN("urn", true),
	VERSION("_version_", null),
	VF1_AUTHOR("vf1_author", null),
	VF1_AUTHOR2("vf1_author2", true),
	VF1_AUTHOR2_ORIG("vf1_author2_orig", true),
	VF1_AUTHOR2_ROLE("vf1_author2-role", true),
	VF1_AUTHOR_CORP("vf1_author_corp", null),
	VF1_AUTHOR_CORP_ORIG("vf1_author_corp_orig", false),
	VF1_AUTHOR_CORP2("vf1_author_corp2", true),
	VF1_AUTHOR_CORP2_ORIG("vf1_author_corp2_orig", true),
	VF1_AUTHOR_ORIG("vf1_author_orig", false),
	ZDB("zdb", false);

	private String fieldName;

	private Boolean multivalue;

	FincSolrSchema(final String fieldNameArg,
	               final Boolean multivalueArg) {

		fieldName = fieldNameArg;
		multivalue = multivalueArg;
	}

	public String getFieldName() {

		return fieldName;
	}

	public Boolean isMultivalue() {

		return multivalue;
	}

	public static FincSolrSchema getFieldByFieldName(final String fieldName) throws CSV2SCLDJException {

		if (fieldName == null) {

			throw new CSV2SCLDJException("field name shouldn't be null");
		}

		for (final FincSolrSchema field : FincSolrSchema.values()) {

			if (field.getFieldName().equals(fieldName)) {

				return field;
			}
		}

		final String message = String.format("couldn't determine finc Solr schema field for '%s'", fieldName);

		throw new CSV2SCLDJException(message);
	}
}
