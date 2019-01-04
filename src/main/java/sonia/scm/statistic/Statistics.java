package sonia.scm.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Modifications;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;

import java.io.IOException;

public class Statistics implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(StatisticData.class);

  private StatisticManager statisticManager;
  private RepositoryService repositoryService;
  private StatisticData statisticData;

  Statistics(StatisticManager statisticManager, RepositoryService repositoryService, StatisticData statisticData) {
    this.statisticManager = statisticManager;
    this.repositoryService = repositoryService;
    this.statisticData = statisticData;
  }

  public RepositoryService getRepositoryService() {
    return repositoryService;
  }

  public Repository getRepository() {
    return repositoryService.getRepository();
  }

  public StatisticData getStatisticData() {
    return statisticData;
  }

  public void add(Changeset c) throws IOException {
    Modifications modifications = repositoryService.getModificationsCommand().revision(c.getId()).getModifications();
    statisticData = statisticData.add(c, modifications);
  }

  public void commit() throws IOException {
    statisticManager.store(this);
  }

  @Override
  public void close() {
    repositoryService.close();
  }
}
