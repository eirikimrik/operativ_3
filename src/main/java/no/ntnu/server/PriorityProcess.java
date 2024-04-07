package no.ntnu.server;

public class PriorityProcess {
  
  private Priority priority;
  private String message;

  public PriorityProcess(Priority priority, String message) {
    this.message = message;
    this.priority = priority;
  }

  public Priority getPriority() {
    return this.priority;
  }

  public String getMessage() {
    return this.message;
  }

}
