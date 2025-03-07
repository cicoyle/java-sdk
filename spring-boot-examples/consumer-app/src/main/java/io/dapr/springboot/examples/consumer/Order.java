/*
 * Copyright 2025 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
limitations under the License.
*/

package io.dapr.springboot.examples.consumer;

public class Order {
  private String id;
  private String item;
  private Integer amount;

  public Order() {
  }

  /**
   * Creates a new Order.
   *
   * @param id     order id
   * @param item   item reference
   * @param amount of items in the order
   */
  public Order(String id, String item, Integer amount) {
    this.id = id;
    this.item = item;
    this.amount = amount;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Order{" + "id='" + id + '\'' + ", item='" + item + '\'' + ", amount=" + amount + '}';
  }
}
