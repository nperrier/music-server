package com.perrier.music.tag;

public class MockTag extends AbstractTag {

	private MockTag(Builder builder) {
		super(builder);
	}

	public final static class Builder extends AbstractTagBuilder<MockTag> {

		@Override
		public MockTag build() {
			return new MockTag(this);
		}

		@Override
		public String toString() {
			return "Builder [toString()=" + super.toString() + "]";
		}
	}
}
