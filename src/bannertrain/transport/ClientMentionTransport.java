package bannertrain.transport;

public class ClientMentionTransport {
	private int offset;
	private int extent;
	private String text;
	private String class_name;
	
	public ClientMentionTransport(int offset, int extent, String class_name, String text)
	{
		this.setOffset(offset);
		this.setExtent(extent);
		this.setText(text);
		this.setClass_name(class_name);
		
	}
	
	public ClientMentionTransport(){};

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the extent
	 */
	public int getExtent() {
		return extent;
	}

	/**
	 * @param extent the extent to set
	 */
	public void setExtent(int extent) {
		this.extent = extent;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the class_name
	 */
	public String getClass_name() {
		return class_name;
	}

	/**
	 * @param class_name the class_name to set
	 */
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	
}
