/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.sorting;

public class SortUtil
{

  /**
   * Used with merge sort. The client application has to implement a stack for the merge sort, that
   * operates only with index positions. How the stack works is commonly not important, but it has
   * to be a fast operation, because the merge sort uses this within the core module very offen.
   * 
   * @see SortUtil#mergeSort(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition,
   *      MergeSortDataModify, boolean)
   * @author Omnaest
   */
  public interface MergeSortDataModify
  {
    public void pushOnStack(int sourceIndexPosition);

    public void popFromStack(int destinationIndexPosition);
  }

  /**
   * Used to wrap the @link {@link MergeSortDataModify} and provide a swap method using the stack.
   * 
   * @see SortUtil#mergeSort(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition,
   *      MergeSortDataModify, boolean)
   * @author Omnaest
   */
  private static class MergeSortDataSwap
  {
    private MergeSortDataModify mergeSortDataModify = null;

    public MergeSortDataSwap(MergeSortDataModify mergeSortDataModify)
    {
      this.mergeSortDataModify = mergeSortDataModify;
    }

    public void swap(int sourceIndexPosition, int destinationIndexPosition)
    {
      this.mergeSortDataModify.pushOnStack(sourceIndexPosition);
      this.mergeSortDataModify.pushOnStack(destinationIndexPosition);
      this.mergeSortDataModify.popFromStack(sourceIndexPosition);
      this.mergeSortDataModify.popFromStack(destinationIndexPosition);
    }
  }

  /**
   * Used within the merge sort core to compare two indexPositions. This will implicitly provide a
   * metric for the index positions.<br> Has to be implemented by the client application.
   * 
   * @see SortUtil#mergeSort(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition,
   *      MergeSortDataModify, boolean)
   * @author Omnaest
   */
  public interface ComparableArbitraryStructureIndexPosition
  {
    public int compare(int indexPosition1, int indexPosition2);
  }

  /**
   * Used by merge sort, to determin the index position start and end points.<br> Has to be
   * implemented by the client application.
   * 
   * @see SortUtil#mergeSort(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition,
   *      MergeSortDataModify, boolean)
   * @author Omnaest
   */

  public interface ArbitraryStructureContext
  {
    public int getStartIndexPosition();

    public int getEndIndexPosition();

  }

  /**
   * Used internally by the merge sort to extend the context by helper methods.
   * 
   * @see SortUtil#mergeSort(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition,
   *      MergeSortDataModify, boolean)
   * @author Omnaest
   */

  private static class ArbitraryStructureContextImpl implements ArbitraryStructureContext
  {
    private int startIndexPosition = -1;
    private int endIndexPosition   = -1;

    public ArbitraryStructureContextImpl(int startIndexPosition, int endIndexPosition)
    {
      this.startIndexPosition = startIndexPosition;
      this.endIndexPosition = endIndexPosition;
    }

    public ArbitraryStructureContextImpl(ArbitraryStructureContext arbitraryStructureContext)
    {
      this(arbitraryStructureContext.getStartIndexPosition(),
           arbitraryStructureContext.getEndIndexPosition());
    }

    /**
     * Returns true if there are less enough index positions left, to use the very fast
     * {@link SortUtil#sortPrimitive(ArbitraryStructureContext, ComparableArbitraryStructureIndexPosition, MergeSortDataModify, boolean)}
     * 
     * @return
     */
    private boolean isToBeSortedPrimitive()
    {
      //
      boolean retval = false;

      //
      if (this.endIndexPosition - this.startIndexPosition + 1 <= 3)
      {
        retval = true;
      }

      //
      return retval;
    }

    @Override
    public int getEndIndexPosition()
    {
      return this.endIndexPosition;
    }

    @Override
    public int getStartIndexPosition()
    {
      return this.startIndexPosition;
    }

  }

  /**
   * Sorts a given arbitrary structure with a mergesort algorithm.<br> The necessary access to the
   * arbitrary structure is the possibility to push (elements of an) index position to a stack,
   * where by this have to implemented by the client application.<br> Also there have to be
   * an index range with uninterrupted range of index positions, and there have to be a metric by a
   * comparison implied. Both have to be implemented by the client application, too.
   * 
   * @see ArbitraryStructureContext
   * @see ComparableArbitraryStructureIndexPosition
   * @see MergeSortDataModify
   * 
   * @param arbitraryStructureContext
   * @param comparableArbitraryStructureIndexPosition
   * @param mergeSortDataModify
   * @param ascending if true, the list will be sorted ascending, if false descending 
   */
  public static void mergeSort(ArbitraryStructureContext arbitraryStructureContext,
                               ComparableArbitraryStructureIndexPosition comparableArbitraryStructureIndexPosition,
                               MergeSortDataModify mergeSortDataModify,
                               boolean ascending)
  {
    //
    ArbitraryStructureContextImpl collectionWideContext = new ArbitraryStructureContextImpl(
                                                                                            arbitraryStructureContext);

    //
    if (!collectionWideContext.isToBeSortedPrimitive())
    {
      //divide into two parts
      int leftPosition = arbitraryStructureContext.getStartIndexPosition();
      int middlePosition = (arbitraryStructureContext.getEndIndexPosition() + arbitraryStructureContext.getStartIndexPosition()) / 2;
      int rightPosition = arbitraryStructureContext.getEndIndexPosition();

      ArbitraryStructureContextImpl leftCollectionContext = new ArbitraryStructureContextImpl(
                                                                                              leftPosition,
                                                                                              middlePosition);
      ArbitraryStructureContextImpl rightCollectionContext = new ArbitraryStructureContextImpl(
                                                                                               middlePosition + 1,
                                                                                               rightPosition);

      //sort both parts
      SortUtil.mergeSort(leftCollectionContext, comparableArbitraryStructureIndexPosition, mergeSortDataModify,
                         ascending);
      SortUtil.mergeSort(rightCollectionContext, comparableArbitraryStructureIndexPosition, mergeSortDataModify,
                         ascending);

      //merge both parts
      SortUtil.mergeSortedParts(leftCollectionContext, rightCollectionContext,
                                comparableArbitraryStructureIndexPosition, mergeSortDataModify, ascending);
    }
    else
    {
      SortUtil.sortPrimitive(arbitraryStructureContext, comparableArbitraryStructureIndexPosition,
                             mergeSortDataModify, ascending);
    }
  }

  /**
   * Merges two sorted parts together in a sorted result.<br> The result will be distributed over
   * the given index ranges from left to right.
   * 
   * @param leftArbitraryStructureContext
   * @param rightArbitraryStructureContext
   * @param comparableArbitraryStructureIndexPosition
   * @param mergeSortDataModify
   */
  private static void mergeSortedParts(ArbitraryStructureContext leftArbitraryStructureContext,
                                       ArbitraryStructureContext rightArbitraryStructureContext,
                                       ComparableArbitraryStructureIndexPosition comparableArbitraryStructureIndexPosition,
                                       MergeSortDataModify mergeSortDataModify,
                                       boolean ascending)
  {
    //
    int compareFactor = ascending ? 1 : -1;

    //
    int leftCurrentIndexPosition = leftArbitraryStructureContext.getStartIndexPosition();
    int rightCurrentIndexPosition = rightArbitraryStructureContext.getStartIndexPosition();

    //
    while (leftCurrentIndexPosition <= leftArbitraryStructureContext.getEndIndexPosition()
           && rightCurrentIndexPosition <= rightArbitraryStructureContext.getEndIndexPosition())
    {
      //compare the two current index positions
      int compare = compareFactor
                    * comparableArbitraryStructureIndexPosition.compare(leftCurrentIndexPosition,
                                                      rightCurrentIndexPosition);

      if (compare > 0)
      {
        mergeSortDataModify.pushOnStack(rightCurrentIndexPosition++);

      }
      else
      {
        mergeSortDataModify.pushOnStack(leftCurrentIndexPosition++);
      }
    }

    //push the rest of the remaining indexpositions on the stack
    while (leftCurrentIndexPosition <= leftArbitraryStructureContext.getEndIndexPosition())
    {
      mergeSortDataModify.pushOnStack(leftCurrentIndexPosition++);
    }
    while (rightCurrentIndexPosition <= rightArbitraryStructureContext.getEndIndexPosition())
    {
      mergeSortDataModify.pushOnStack(rightCurrentIndexPosition++);
    }

    //pop all from the stack from right to left
    for (int iCurrentIndexPosition = rightArbitraryStructureContext.getEndIndexPosition(); iCurrentIndexPosition >= rightArbitraryStructureContext.getStartIndexPosition(); iCurrentIndexPosition--)
    {
      mergeSortDataModify.popFromStack(iCurrentIndexPosition);
    }
    for (int iCurrentIndexPosition = leftArbitraryStructureContext.getEndIndexPosition(); iCurrentIndexPosition >= leftArbitraryStructureContext.getStartIndexPosition(); iCurrentIndexPosition--)
    {
      mergeSortDataModify.popFromStack(iCurrentIndexPosition);
    }

  }

  /**
   * Allows to sort a arbitrary structure up to a size of 3 elements.
   * 
   * @param arbitraryStructureContext
   * @param comparableArbitraryStructureIndexPosition
   * @param mergeSortDataModify
   */
  private static void sortPrimitive(ArbitraryStructureContext arbitraryStructureContext,
                                    ComparableArbitraryStructureIndexPosition comparableArbitraryStructureIndexPosition,
                                    MergeSortDataModify mergeSortDataModify,
                                    boolean ascending)
  {
    //
    int collectionSize = arbitraryStructureContext.getEndIndexPosition()
                         - arbitraryStructureContext.getStartIndexPosition() + 1;
    int orderFactor = ascending ? 1 : -1;

    if (collectionSize == 2)
    {
      int indexPosition1 = arbitraryStructureContext.getStartIndexPosition();
      int indexPosition2 = arbitraryStructureContext.getEndIndexPosition();
      int compare = orderFactor * comparableArbitraryStructureIndexPosition.compare(indexPosition1, indexPosition2);
      if (compare > 0)
      {
        new MergeSortDataSwap(mergeSortDataModify).swap(indexPosition1, indexPosition2);
      }
    }
    else if (collectionSize == 3)
    {
      SortUtil.sortPrimitive(
                             new ArbitraryStructureContextImpl(
                                                               arbitraryStructureContext.getStartIndexPosition(),
                                                               arbitraryStructureContext.getStartIndexPosition() + 1),
                             comparableArbitraryStructureIndexPosition, mergeSortDataModify, ascending);
      SortUtil.sortPrimitive(
                             new ArbitraryStructureContextImpl(
                                                               arbitraryStructureContext.getStartIndexPosition() + 1,
                                                               arbitraryStructureContext.getStartIndexPosition() + 2),
                             comparableArbitraryStructureIndexPosition, mergeSortDataModify, ascending);
      SortUtil.sortPrimitive(
                             new ArbitraryStructureContextImpl(
                                                               arbitraryStructureContext.getStartIndexPosition(),
                                                               arbitraryStructureContext.getStartIndexPosition() + 1),
                             comparableArbitraryStructureIndexPosition, mergeSortDataModify, ascending);
    }
  }
}
