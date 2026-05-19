package com.blissfuljuan.aiprojecteval.documentevaluation.config;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AllowedFileTypeSchemaSynchronizer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AllowedFileTypeSchemaSynchronizer.class);

	private static final List<TableConstraint> FILE_TYPE_TABLES = List.of(
			new TableConstraint(
					"preset_document_requirement_allowed_file_types",
					"preset_document_requirement_allowed_file_types_file_type_check"),
			new TableConstraint(
					"document_requirement_allowed_file_types",
					"document_requirement_allowed_file_types_file_type_check")
	);

	private final JdbcTemplate jdbcTemplate;

	public AllowedFileTypeSchemaSynchronizer(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!isPostgreSql()) {
			return;
		}

		for (TableConstraint tableConstraint : FILE_TYPE_TABLES) {
			if (!tableExists(tableConstraint.tableName())) {
				continue;
			}
			synchronizeFileTypeConstraint(tableConstraint);
		}
	}

	private boolean isPostgreSql() {
		return Boolean.TRUE.equals(jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
			DatabaseMetaData metaData = connection.getMetaData();
			return metaData.getDatabaseProductName().toLowerCase(Locale.ROOT).contains("postgresql");
		}));
	}

	private boolean tableExists(String tableName) {
		return Boolean.TRUE.equals(jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
			try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, new String[] { "TABLE" })) {
				return resultSet.next();
			}
		}));
	}

	private void synchronizeFileTypeConstraint(TableConstraint tableConstraint) {
		if (!columnExists(tableConstraint.tableName(), "file_type")) {
			log.warn("Skipping {} file_type constraint synchronization because column file_type does not exist",
					tableConstraint.tableName());
			return;
		}

		jdbcTemplate.execute("alter table " + tableConstraint.tableName()
				+ " drop constraint if exists " + tableConstraint.constraintName());

		List<String> checkConstraints = jdbcTemplate.queryForList("""
				select c.conname
				from pg_constraint c
				join pg_class t on t.oid = c.conrelid
				join pg_namespace n on n.oid = t.relnamespace
				where n.nspname = current_schema()
				  and t.relname = ?
				  and c.contype = 'c'
				  and pg_get_constraintdef(c.oid) like '%file_type%'
				""", String.class, tableConstraint.tableName());

		for (String constraintName : checkConstraints) {
			if (constraintName.toLowerCase(Locale.ROOT).contains("not_null")) {
				continue;
			}
			jdbcTemplate.execute("alter table " + tableConstraint.tableName()
					+ " drop constraint if exists " + constraintName);
		}

		jdbcTemplate.execute("alter table " + tableConstraint.tableName()
				+ " alter column file_type type varchar(255) using file_type::varchar");

		jdbcTemplate.execute("alter table " + tableConstraint.tableName()
				+ " add constraint " + tableConstraint.constraintName()
				+ " check (file_type in (" + allowedFileTypeSqlValues() + "))");

		log.info("Synchronized {} file_type constraint with AllowedFileType enum values",
				tableConstraint.tableName());
	}

	private boolean columnExists(String tableName, String columnName) {
		return Boolean.TRUE.equals(jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
			try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, columnName)) {
				return resultSet.next();
			}
		}));
	}

	private String allowedFileTypeSqlValues() {
		return Arrays.stream(AllowedFileType.values())
				.map(Enum::name)
				.map(value -> "'" + value + "'")
				.collect(Collectors.joining(", "));
	}

	private record TableConstraint(String tableName, String constraintName) {
	}
}
