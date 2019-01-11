package sonia.scm.statistic.resources;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

public class IndexDto extends HalRepresentation {
  public IndexDto(Links links) {
    add(links);
  }
}
