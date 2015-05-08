/*
 * Copyright 2001-2015 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.prop

import scala.collection.mutable.ListBuffer

trait Gen[T] { thisGenOfT =>
  def next(size: Int = 100, rnd: Rnd = Rnd.default): (T, Rnd)
  def map[U](f: T => U): Gen[U] =
    new Gen[U] {
      def next(size: Int, rnd: Rnd): (U, Rnd) = {
        val (nextT, nextRnd) = thisGenOfT.next(size, rnd)
        (f(nextT), nextRnd)
      }
    }
  def flatMap[U](f: T => Gen[U]): Gen[U] = 
    new Gen[U] { thisInnerGen =>
      def next(size: Int, rnd: Rnd): (U, Rnd) = {
        val (nextT, nextRnd) = thisGenOfT.next(size, rnd)
        val (a, b) = f(nextT).next(size, nextRnd)
        (a, b)
      }
    }
}

object Gen {
  def chooseInt(from: Int, to: Int): Gen[Int] =
    new Gen[Int] { thisIntGen =>
      def next(size: Int, rnd: Rnd): (Int, Rnd) = {
        val (nextInt, nextRnd) = rnd.chooseInt(from, to)
        (nextInt, nextRnd)
      }
    }
  private final class IntGen extends Gen[Int] {
    def next(size: Int, rnd: Rnd): (Int, Rnd) = rnd.nextIntWithEdges
  }

  implicit val intGen: Gen[Int] = new IntGen
    // new IntGen(List(Int.MinValue, -1, 0, 1, Int.MaxValue))
    // new IntGen(scala.util.Random.shuffle(List(Int.MinValue, -1, 0, 1, Int.MaxValue)))

  private final class DoubleGen extends Gen[Double] {
    def next(size: Int, rnd: Rnd): (Double, Rnd) = rnd.nextDoubleWithEdges
  }
  implicit val doubleGen: Gen[Double] = new DoubleGen
}

