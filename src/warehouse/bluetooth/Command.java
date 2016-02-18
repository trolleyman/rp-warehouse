package warehouse.bluetooth;

public class Command {

  private String name;
  private String data;
  private String source;
  private String recipient;
  
  public Command(String _name, String _data, String _source, String _recipient) {
    this.name = _name;
    this.data = _data;
    this.source = _source;
    this.recipient = _recipient;
  }
  
  public Command(String _name, String _data) {
    this(_name, _data, null, null);
  }
  
  public Command(String _name) {
    this(_name, null, null, null);
  }

  public String getName() {
    return name;
  }

  public String getData() {
    return data;
  }

  public String getSource() {
    return source;
  }

  public String getRecipient() {
    return recipient;
  }
}
