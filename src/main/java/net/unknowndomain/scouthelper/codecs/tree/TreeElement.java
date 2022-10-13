/*
 * Copyright 2022 Marco Bignami.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.unknowndomain.scouthelper.codecs.tree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author m.bignami
 */
public class TreeElement
{
    private final String index;
    private final List<TreeElement> elements;
    
    public TreeElement(String index, int format, int radix, int base, int totalLeaves, int maxBranches)
    {
        this.elements = new LinkedList<>();
        this.index = index;
        BigDecimal tl = new BigDecimal(totalLeaves);
        BigDecimal mb = new BigDecimal(maxBranches);
        int next = tl.divide(mb, 0, RoundingMode.CEILING).intValue();
        int idx = 0;
        if (totalLeaves > 1)
        {
            while(totalLeaves > 0)
            {
                String prx = StringUtils.leftPad(Integer.toString(base+idx++, radix), format, '0');
                int tmp = totalLeaves - next;
                if (tmp >= 0)
                {
                    this.elements.add(new TreeElement(prx, format, radix, base, next, maxBranches));
                }
                else
                {
                    this.elements.add(new TreeElement(prx, format, radix, base, totalLeaves, maxBranches));
                }
                totalLeaves = tmp;
            }
        }
    }
    
    public boolean isLeaf()
    {
        return (elements == null) || (elements.isEmpty());
    }
    
    public int maxDepth()
    {
        if (isLeaf())
        {
            return 0;
        }
        int maxDepth = 0;
        for (TreeElement leaf : elements)
        {
            int tmp = 1 + leaf.maxDepth();
            if (tmp > maxDepth)
            {
                maxDepth = tmp;
            }
        }
        return maxDepth;
    }
    
    public List<String> buildList()
    {
        List<String> list = new LinkedList<>();
        if (isLeaf())
        {
            list.add(index);
            return list;
        }
        for(TreeElement leaf : elements)
        {
            for (String leafIdx : leaf.buildList())
            {
                list.add(index + leafIdx);
            }
        }
        return list;
    }

    public String getIndex()
    {
        return index;
    }

    public List<TreeElement> getElements()
    {
        return elements;
    }
}
