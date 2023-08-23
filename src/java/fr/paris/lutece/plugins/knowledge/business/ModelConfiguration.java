/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.knowledge.business;

import java.net.Proxy;
import java.time.Duration;

public class ModelConfiguration
{
    private String modelName;
    private Double temperature;
    private Double topP;
    private Integer maxTokens;
    private Double presencePenalty;
    private Double frequencyPenalty;
    static String [ ] parameters = {
            "modelName", "temperature", "topP", "maxTokens", "presencePenalty", "frequencyPenalty"
    };

    // Constructors
    public ModelConfiguration( )
    {
    }

    public ModelConfiguration( String modelName, Double temperature, Double topP, Integer maxTokens, Double presencePenalty, Double frequencyPenalty )
    {
        this.modelName = modelName;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
    }

    // Getters and Setters

    public String getModelName( )
    {
        return modelName;
    }

    public void setModelName( String modelName )
    {
        this.modelName = modelName;
    }

    public Double getTemperature( )
    {
        return temperature;
    }

    public void setTemperature( Double temperature )
    {
        this.temperature = temperature;
    }

    public Double getTopP( )
    {
        return topP;
    }

    public void setTopP( Double topP )
    {
        this.topP = topP;
    }

    public Integer getMaxTokens( )
    {
        return maxTokens;
    }

    public void setMaxTokens( Integer maxTokens )
    {
        this.maxTokens = maxTokens;
    }

    public Double getPresencePenalty( )
    {
        return presencePenalty;
    }

    public void setPresencePenalty( Double presencePenalty )
    {
        this.presencePenalty = presencePenalty;
    }

    public Double getFrequencyPenalty( )
    {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty( Double frequencyPenalty )
    {
        this.frequencyPenalty = frequencyPenalty;
    }

    public static String [ ] getParameters( )
    {
        return parameters;
    }

    public void setConfigValue( String param, String value )
    {
        switch( param )
        {
            case "modelName":
                setModelName( value );
                break;
            case "temperature":
                setTemperature( Double.parseDouble( value ) );
                break;
            case "topP":
                setTopP( Double.parseDouble( value ) );
                break;
            case "maxTokens":
                setMaxTokens( Integer.parseInt( value ) );
                break;
            case "presencePenalty":
                setPresencePenalty( Double.parseDouble( value ) );
                break;
            case "frequencyPenalty":
                setFrequencyPenalty( Double.parseDouble( value ) );
                break;
            default:
                break;
        }
    }

}
