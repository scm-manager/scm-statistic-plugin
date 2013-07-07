/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.statistic;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import sonia.scm.AbstractTestBase;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.PermissionType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.util.MockUtil;

import static org.mockito.Mockito.*;

/**
 * @author Sebastian Sdorra
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RepositoryServiceFactory.class)
public class StatisticManagerTest extends AbstractTestBase
{

  /**
   * Method description
   *
   */
  @Test(expected = UnauthorizedException.class)
  public void testCheckPermissions()
  {
    setSubject();

    StatisticManager manager = create(false);

    manager.checkPermissions(new Repository(), PermissionType.READ);
  }

  /**
   * Method description
   *
   */
  @Test(expected = UnauthorizedException.class)
  public void testCheckPermissionsPublicReadableFailure()
  {
    setSubject();

    StatisticManager manager = create(true);

    manager.checkPermissions(new Repository(), PermissionType.READ);
  }

  /**
   * Method description
   *
   */
  @Test
  public void testCheckPermissionsPublicReadableSuccess()
  {
    setSubject();

    StatisticManager manager = create(true);
    Repository repository = new Repository();

    repository.setPublicReadable(true);

    manager.checkPermissions(repository, PermissionType.READ);
  }

  /**
   * Method description
   *
   *
   * @param anonymousAccess
   *
   * @return
   */
  private StatisticManager create(boolean anonymousAccess)
  {
    ScmConfiguration configuration = new ScmConfiguration();

    configuration.setAnonymousAccessEnabled(anonymousAccess);

    RepositoryServiceFactory repositoryServiceFactory =
      PowerMockito.mock(RepositoryServiceFactory.class);
    DataStoreFactory dataStoreFactory = mock(DataStoreFactory.class);
    DataStore store = mock(DataStore.class);

    //J-
    when(
      dataStoreFactory.getStore(
        any(Class.class),
        anyString()
      )
    ).thenReturn(store);
    //J+

    return new StatisticManager(configuration, repositoryServiceFactory,
      dataStoreFactory);
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   */
  private void setSubject()
  {
    setSubject(
      MockUtil.createUserSubject(
        new DefaultSecurityManager(new SimpleAccountRealm())));
  }
}
