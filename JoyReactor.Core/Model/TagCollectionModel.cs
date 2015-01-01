﻿using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class TagCollectionModel
    {
        public static event EventHandler InvalidateEvent;

        public static void OnInvalidateEvent()
        {
            InvalidateEvent?.Invoke(null, null);
        }

        SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        [Obsolete]
        public Task<List<Tag>> GetMainSubscriptionsAsync()
        {
            return DoGetSubscriptionsAsync();
        }

        public IObservable<List<Tag>> GetMainSubscriptions()
        {
            var first = Observable.FromAsync(DoGetSubscriptionsAsync);
            var second = Observable
                .FromEventPattern(typeof(TagCollectionModel), "InvalidateEvent")
                .SelectMany(_ => Observable.FromAsync(DoGetSubscriptionsAsync));
            return first.Concat(second);
        }

        Task<List<Tag>> DoGetSubscriptionsAsync()
        {
            return Task.Run(() =>
                connection.SafeQuery<Tag>("SELECT * FROM tags WHERE Flags & ? != 0", Tag.FlagShowInMain));
        }

        public Task<List<TagLinkedTag>> GetTagLinkedTagsAsync(ID tagId)
        {
            return Task.Run(() =>
            {
                return connection.SafeQuery<TagLinkedTag>(
                    "SELECT * FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                    MainDb.ToFlatId(tagId));
            });
        }
    }
}