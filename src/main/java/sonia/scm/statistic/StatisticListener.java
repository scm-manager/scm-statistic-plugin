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

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEvent;
import sonia.scm.event.Subscriber;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.repository.RepositoryHookEvent;
import sonia.scm.repository.RepositoryHookType;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
@Subscriber(async = true)
public class StatisticListener
{

  /**
   * the logger for StatisticListener
   */
  private static final Logger logger =
    LoggerFactory.getLogger(StatisticListener.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param statisticManager
   */
  @Inject
  public StatisticListener(StatisticManager statisticManager)
  {
    this.statisticManager = statisticManager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param event
   */
  @Subscribe
  public void onHookEvent(RepositoryHookEvent event)
  {
    if (event.getType() == RepositoryHookType.POST_RECEIVE)
    {
      handleHookEvent(event);
    }
  }

  /**
   * Method description
   *
   *
   * @param event
   */
  @Subscribe
  public void onRepositoryEvent(RepositoryEvent event)
  {
    if (event.getEventType() == HandlerEvent.DELETE)
    {
      Repository repository = event.getItem();

      logger.trace("receive delete event for repository {}",
        repository.getId());
      statisticManager.remove(repository);
    }

  }

  /**
   * Method description
   *
   *
   * @param event
   */
  private void handleHookEvent(RepositoryHookEvent event)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("update statistic of repository {}",
        event.getRepository().getName());
    }

    try
    {
      Set<String> collectedChangesets = Sets.newHashSet();
      StatisticData data = statisticManager.get(event.getRepository());

      for (Changeset c : event.getChangesets())
      {
        if (!collectedChangesets.contains(c.getId()))
        {
          data.add(c);
          collectedChangesets.add(c.getId());
        }
        else
        {
          logger.trace("changeset {} already added to statistic", c.getId());
        }
      }

      statisticManager.store(event.getRepository(), data);
    }
    catch (IOException ex)
    {
      logger.error("could not update statistic", ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private StatisticManager statisticManager;
}
