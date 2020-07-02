package it.polimi.vovarini.view.cli.elements;

import it.polimi.vovarini.view.cli.styling.Color;
import it.polimi.vovarini.view.cli.styling.TextStyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A list of elements where you can move up and down with W/S and
 * select a number of elements with space.
 * The list uses the {@link T#toString()} method to print each element.
 * @param <T> The class of objects in the list.
 */
public class MultiChoiceList<T> extends CLIElement {

  private final List<T> options;
  private int currentOptionIndex;

  private final Set<T> selectedOptions;
  private final int maxChoices;

  /**
   * Creates a MultiChoiceList where you can select at most {@code maxChoices} elements.
   * @param options A list of all options.
   * @param maxChoices The maximum number of choices.
   */
  public MultiChoiceList(List<T> options, int maxChoices) {
    this.options = options;
    this.maxChoices = maxChoices;

    currentOptionIndex = 0;
    selectedOptions = new HashSet<>();
  }

  public Set<T> getSelectedOptions() {
    return new HashSet<>(selectedOptions);
  }

  /**
   * Moves the cursor up.
   */
  public void moveUp() {
    currentOptionIndex--;
    if (currentOptionIndex < 0) {
      currentOptionIndex = options.size() - 1;
    }
  }

  /**
   * Moves the cursor down.
   */
  public void moveDown() {
    currentOptionIndex++;
    if (currentOptionIndex >= options.size()) {
      currentOptionIndex = 0;
    }
  }

  /**
   * Selects the element currently under the cursor.
   */
  public void select() {
    T current = options.get(currentOptionIndex);
    if (selectedOptions.contains(current)) {
      selectedOptions.remove(current);
    } else if (selectedOptions.size() < maxChoices) {
      selectedOptions.add(current);
    }
  }

  @Override
  public String render() {
    StringBuilder content = new StringBuilder();

    for (T option : options) {
      String line = options.get(currentOptionIndex).equals(option) ?
              Color.Black.fgWrap(Color.White.bgWrap(option.toString())) : option.toString();
      content.append(selectedOptions.contains(option) ?
              TextStyle.bold(line) : line)
              .append("\n");
    }

    return content.toString();
  }

  /**
   * @return Whether the maximum number of choices has been reached.
   */
  public boolean maxSelected() {
    return selectedOptions.size() >= maxChoices;
  }
}
