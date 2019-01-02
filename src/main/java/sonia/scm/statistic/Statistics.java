package sonia.scm.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Modifications;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;


public class Statistics {

  Repository repository;
  RepositoryServiceFactory repositoryServiceFactory;
  StatisticData statisticData;

  private static final Logger logger =
    LoggerFactory.getLogger(StatisticData.class);

  public Statistics(Repository repository, RepositoryServiceFactory repositoryServiceFactory){
    this.repository = repository;
    this.repositoryServiceFactory = repositoryServiceFactory;
    this.statisticData = new StatisticData();
  }

  public void add(Changeset c){
    try {
      Modifications mods = repositoryServiceFactory.create(repository).getModificationsCommand().getModifications();
      statisticData = statisticData.add(c, mods);
    }
    catch(IOException ex) {
      logger.error("could not get modifications", ex);
    }
  }
}
