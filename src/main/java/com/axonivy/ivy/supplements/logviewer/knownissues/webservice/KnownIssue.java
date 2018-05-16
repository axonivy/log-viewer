package com.axonivy.ivy.supplements.logviewer.knownissues.webservice;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KnownIssue {
	public String issueID;
	public String issueTitle;
	public List<String> fixedVersions;
	public String workaround;
	public List<String> matchExceptionParts;

	public KnownIssue(String issueID, String issueTitle, List<String> fixedVersions, String workaround,
			List<String> matchExceptionParts) {
		super();
		this.issueID = issueID;
		this.issueTitle = issueTitle;
		this.fixedVersions = fixedVersions;
		this.workaround = workaround;
		this.matchExceptionParts = matchExceptionParts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!KnownIssue.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final KnownIssue toCompare = (KnownIssue) obj;

		return new EqualsBuilder()
				.append(issueID, toCompare.issueID)
				.append(issueTitle, toCompare.issueTitle)
				.append(fixedVersions, toCompare.fixedVersions)
				.append(workaround, toCompare.workaround)
				.append(matchExceptionParts, toCompare.matchExceptionParts)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(issueID)
				.append(issueTitle)
				.append(fixedVersions)
				.append(workaround)
				.append(matchExceptionParts)
				.toHashCode();
	}
}
