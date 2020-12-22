package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding.rule.RuleConfiguration;

public interface YamlRuleConfiguration extends YamlConfiguration {

    /**
     * Get rule configuration type.
     *
     * @return rule configuration type
     */
    Class<? extends RuleConfiguration> getRuleConfigurationType();
}