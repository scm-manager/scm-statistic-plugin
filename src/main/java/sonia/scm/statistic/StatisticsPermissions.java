package sonia.scm.statistic;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public class StatisticsPermissions {


  public static boolean isComputeForAllReposPermitted() {
    return RepositoryPermissions.custom(StatisticManager.ACTION_COMPUTE_STATISTICS).isPermitted();
  }

  public static boolean isComputeForRepoPermitted(Repository repository) {
    return RepositoryPermissions.custom(StatisticManager.ACTION_COMPUTE_STATISTICS, repository).isPermitted();
  }

  public static boolean isReadForAllReposPermitted() {
    return RepositoryPermissions.custom(StatisticManager.ACTION_READ_STATISTICS).isPermitted();
  }

  public static boolean isReadForRepoPermitted(Repository repository) {
    return RepositoryPermissions.custom(StatisticManager.ACTION_READ_STATISTICS, repository).isPermitted();
  }
}
