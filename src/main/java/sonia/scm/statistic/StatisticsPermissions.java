package sonia.scm.statistic;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public class StatisticsPermissions {

  private  StatisticsPermissions() {
  }

  public static boolean isComputePermitted(Repository repository) {
    return RepositoryPermissions.custom(StatisticManager.ACTION_COMPUTE_STATISTICS).isPermitted() ||
    RepositoryPermissions.custom(StatisticManager.ACTION_COMPUTE_STATISTICS, repository).isPermitted();
  }

  public static boolean isReadPermitted(Repository repository) {
    return RepositoryPermissions.custom(StatisticManager.ACTION_READ_STATISTICS).isPermitted() ||
    RepositoryPermissions.custom(StatisticManager.ACTION_READ_STATISTICS, repository).isPermitted();
  }
}
