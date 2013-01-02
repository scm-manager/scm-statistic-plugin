/*
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
Ext.chart.Chart.CHART_URL = 'resources/extjs/resources/charts.swf';

Ext.ns('Sonia.statistic');

Sonia.statistic.LinkPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {

  initComponent: function(){
    var links = [{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'Commits per Author',
      handler: this.openCommitsPerAuthor,
      scope: this
    },{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'Commits per Month',
      handler: this.openCommitsPerMonth,
      scope: this
    },{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'Commits per Weekday',
      handler: this.openCommitsPerWeekday,
      scope: this
    },{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'Commits per Hour',
      handler: this.openCommitsPerHour,
      scope: this
    },{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'Top modified Files',
      handler: this.openTopModifiedFiles,
      scope: this
    },{
      xtype: 'link',
      style: 'font-weight: bold',
      text: 'File modification count',
      handler: this.openFileModificationCount,
      scope: this
    }];
  
    if ( admin ){
      links.push({
        xtype: 'link',
        style: 'font-weight: bold',
        text: 'Rebuild statistic',
        handler: this.rebuildStatistic,
        scope: this
      });
    }
    
    var config = {
      title: 'Statistics',
      padding: 5,
      bodyCssClass: 'x-panel-mc',
      layout: 'table',
      layoutConfig: {
        columns: 1
      },
      defaults: {
        style: 'font-size: 12px'
      },
      items: links
    }
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.statistic.LinkPanel.superclass.initComponent.apply(this, arguments);
  },
  
  rebuildStatistic: function(){
    Ext.MessageBox.show({
      title: 'Rebuild statistic',
      msg: 'Rebuild statistic for repository ' + this.item.name + '?',
      buttons: Ext.MessageBox.OKCANCEL,
      icon: Ext.MessageBox.QUESTION,
      fn: function(result){
        if ( result == 'ok' ){

          if ( debug ){
            console.debug('rebuild statistic for ' + this.item.name);
          }
          
          var el = this.el;
          var tid = setTimeout( function(){el.mask('Loading ...');}, 100);

          Ext.Ajax.request({
            url: restUrl + 'plugins/statistic/' + this.item.id + '/rebuild.json',
            method: 'POST',
            scope: this,
            success: function(){
              clearTimeout(tid);
              el.unmask();
            },
            failure: function(result){
              clearTimeout(tid);
              el.unmask();
              main.handleFailure(
                result.status, 
                'Rebuild failed', 
                'Could not rebuild statistic'
              );
            }
          });
        } // canceled
      },
      scope: this
    });
  },
  
  openCommitsPerMonth: function(){
    main.addTab({
      id: 'commitsPerMonth;' + this.item.id,
      xtype: 'statisticCommitsPerMonthPanel',
      repository: this.item,
      closable: true
    });
  },
  
  openCommitsPerWeekday: function(){
    main.addTab({
      id: 'commitsPerWeekday;' + this.item.id,
      xtype: 'statisticCommitsPerWeekdayPanel',
      repository: this.item,
      closable: true
    });
  },
  
  openCommitsPerAuthor: function(){
    main.addTab({
      id: 'commitsPerAuthor;' + this.item.id,
      xtype: 'statisticCommitsPerAuthorPanel',
      repository: this.item,
      closable: true
    });
  },
  
  openCommitsPerHour: function(){
    main.addTab({
      id: 'commitsPerHour;' + this.item.id,
      xtype: 'statisticCommitsPerHourPanel',
      repository: this.item,
      closable: true
    });
  },
  
  openTopModifiedFiles: function(){
    main.addTab({
      id: 'topModifiedFiles;' + this.item.id,
      xtype: 'statisticTopModifiedFilesPanel',
      repository: this.item,
      closable: true
    });
  },
  
  openFileModificationCount: function(){
    main.addTab({
      id: 'fileModificationCount;' + this.item.id,
      xtype: 'statisticFileModificationCount',
      repository: this.item,
      closable: true
    });
  }
  
});

// register xtype
Ext.reg("statisticLinkPanel", Sonia.statistic.LinkPanel);

// register panel
Sonia.repository.openListeners.push(function(repository, panels){
  panels.push({
    xtype: 'statisticLinkPanel',
    item: repository
  });
});