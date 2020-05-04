package it.polimi.vovarini.view.cli.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiChoiceList<T> extends CLIElement {

  private final List<T> options;

  private final Set<T> selectedOptions;
  private final int maxChoices;

  public MultiChoiceList(List<T> options, int maxChoices){
    this.options = options;
    this.maxChoices = maxChoices;
    selectedOptions = new HashSet<>();
  }

  public void select(T option){
    if (options.contains(option) && selectedOptions.size() < maxChoices) {
      this.selectedOptions.add(option);
    }
  }

  public void deSelect(T option){
    selectedOptions.remove(option);
  }

  public Set<T> getSelectedOptions() {
    return new HashSet<>(selectedOptions);
  }

  @Override
  public String render() {
    return null;
  }
}
