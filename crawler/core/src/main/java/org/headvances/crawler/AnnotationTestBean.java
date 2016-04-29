package org.headvances.crawler;

/**
 * $Author: Tuan Nguyen$ 
 **/
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(
	objectName="org.headvances.crawler:name=AnnotationTestBean", 
	description="My Managed Bean", 
	log=true, logFile="jmx.log", currencyTimeLimit=15, 
	persistPolicy="OnUpdate", persistPeriod=200, persistLocation="foo", persistName="bar"
)
public class AnnotationTestBean  {
  private String name;
  private int age;

  @ManagedAttribute(description="The Age Attribute", currencyTimeLimit=15)
  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }

  @ManagedAttribute(
  	description="The Name Attribute", currencyTimeLimit=20, 
  	defaultValue="bar", persistPolicy="OnUpdate"
  )
  public void setName(String name) {
    this.name = name;
  }

  @ManagedAttribute(defaultValue="foo", persistPeriod=300)
  public String getName() { return name; }

  @ManagedOperation(description="Add two numbers")
  @ManagedOperationParameters({
    @ManagedOperationParameter(name = "x", description = "The first frequency"),
    @ManagedOperationParameter(name = "y", description = "The second frequency")
  })
  public int add(int x, int y) {
    return x + y;
  }

  public void dontExposeMe() { throw new RuntimeException(); }
}