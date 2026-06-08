package de.gupta.automation.task.framework.rest.openapi;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;
import de.gupta.automation.task.framework.registry.TaskRegistry;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration(proxyBeanMethods = false)
public class FrameworkOpenApiConfiguration
{
	@Bean
	OpenApiCustomizer taskDescriptorOpenApiCustomizer(final TaskRegistry taskRegistry)
	{
		return openApi -> customise(openApi, taskRegistry);
	}

	private static void customise(final OpenAPI openApi, final TaskRegistry taskRegistry)
	{
		final Components components = openApi.getComponents() == null ? new Components() : openApi.getComponents();
		final Paths paths = openApi.getPaths() == null ? new Paths() : openApi.getPaths();

		for (final TaskDescriptor<?, ?, ?> descriptor : taskRegistry.descriptors())
		{
			registerTaskDocumentation(descriptor, components, paths);
		}

		openApi.setComponents(components);
		openApi.setPaths(paths);
	}

	private static void registerTaskDocumentation(final TaskDescriptor<?, ?, ?> descriptor,
	                                              final Components components,
	                                              final Paths paths)
	{
		final String requestSchemaName = schemaName(descriptor, "Request");
		final String responseSchemaName = schemaName(descriptor, "Response");
		registerRequestSchema(descriptor, components, requestSchemaName);
		registerResponseSchema(descriptor, components, responseSchemaName);

		final RequestBody requestBody = new RequestBody()
				.required(true)
				.description(descriptor.rest().description())
				.content(new Content().addMediaType(
						org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
						new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + requestSchemaName))));

		final ApiResponse response = new ApiResponse()
				.description("Successful task execution.")
				.content(new Content().addMediaType(
						descriptor.rest().produces(),
						new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + responseSchemaName))));

		final Operation operation = new Operation()
				.operationId(operationId(descriptor))
				.summary(descriptor.name())
				.description(descriptor.rest().description())
				.requestBody(requestBody)
				.responses(new ApiResponses().addApiResponse("200", response))
				.addTagsItem("tasks");

		paths.addPathItem(descriptor.rest().path(), new PathItem().post(operation));
	}

	private static void registerRequestSchema(final TaskDescriptor<?, ?, ?> descriptor,
	                                          final Components components,
	                                          final String schemaName)
	{
		final ObjectSchema schema = new ObjectSchema();
		final List<String> required = new ArrayList<>();
		for (final TaskOptionDescriptor option : descriptor.rest().options())
		{
			final Schema<?> propertySchema = toSchema(option.valueType(), components);
			propertySchema.setDescription(option.description());
			if (option.documentedDefaultValue() != null)
			{
				propertySchema.setDefault(option.documentedDefaultValue());
			}
			schema.addProperties(option.restName(), propertySchema);
			if (option.mandatory())
			{
				required.add(option.restName());
			}
		}
		if (!required.isEmpty())
		{
			schema.setRequired(required);
		}
		components.addSchemas(schemaName, schema);
	}

	private static void registerResponseSchema(final TaskDescriptor<?, ?, ?> descriptor,
	                                           final Components components,
	                                           final String schemaName)
	{
		components.addSchemas(schemaName, toSchema(descriptor.outputType(), components));
	}

	private static String schemaName(final TaskDescriptor<?, ?, ?> descriptor, final String suffix)
	{
		return Arrays.stream(descriptor.name().split("-"))
		             .filter(part -> !part.isBlank())
		             .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
		             .reduce("", String::concat) + suffix;
	}

	private static String operationId(final TaskDescriptor<?, ?, ?> descriptor)
	{
		return "execute" + schemaName(descriptor, "");
	}

	private static Schema<?> toSchema(final Class<?> valueType, final Components components)
	{
		if (valueType == String.class || valueType == Character.class || valueType == char.class)
		{
			return new StringSchema();
		}
		if (valueType == boolean.class || valueType == Boolean.class)
		{
			return new BooleanSchema();
		}
		if (valueType == int.class || valueType == Integer.class || valueType == short.class || valueType == Short.class)
		{
			return new IntegerSchema().format("int32");
		}
		if (valueType == long.class || valueType == Long.class)
		{
			return new IntegerSchema().format("int64");
		}
		if (valueType == float.class || valueType == Float.class)
		{
			return new NumberSchema().format("float");
		}
		if (valueType == double.class || valueType == Double.class)
		{
			return new NumberSchema().format("double");
		}
		if (valueType.isEnum())
		{
			return new StringSchema()._enum(Arrays.stream(valueType.getEnumConstants())
			                                      .filter(Objects::nonNull)
			                                      .map(Object::toString)
			                                      .toList());
		}
		if (valueType.isArray())
		{
			return new ArraySchema().items(toSchema(valueType.getComponentType(), components));
		}
		if (Iterable.class.isAssignableFrom(valueType))
		{
			return new ArraySchema().items(new ObjectSchema());
		}

		final ResolvedSchema resolvedSchema =
				ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(valueType));
		if (resolvedSchema.referencedSchemas != null && !resolvedSchema.referencedSchemas.isEmpty())
		{
			final Map<String, Schema> schemas = components.getSchemas() == null
					? new LinkedHashMap<>()
					: new LinkedHashMap<>(components.getSchemas());
			schemas.putAll(resolvedSchema.referencedSchemas);
			components.setSchemas(schemas);
		}
		if (resolvedSchema.schema != null)
		{
			return resolvedSchema.schema;
		}
		return new ObjectSchema().description(valueType.getSimpleName());
	}
}