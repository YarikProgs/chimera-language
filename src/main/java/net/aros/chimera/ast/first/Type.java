package net.aros.chimera.ast.first;

import net.aros.chimera.ast.SourcePos;

import java.util.List;

public sealed interface Type extends Node {
    // Nullable
    record NullableType(Type type, SourcePos pos) implements Type {}
    // Binary
    record IntersectionType(Type left, Type right, SourcePos pos) implements Type {}
    record UnionType(Type left, Type right, SourcePos pos) implements Type {}
    // Atoms
    record TupleType(List<Type> types, SourcePos pos) implements Type {}
    record FunctionType(List<Type> params, Type returnType, SourcePos pos) implements Type {}
    record ListType(Type type, SourcePos pos) implements Type {}
    record MapType(Type keyType, Type valueType, SourcePos pos) implements Type {}
    record IdentifierType(String name, SourcePos pos) implements Type {}
}