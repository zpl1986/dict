
package cn.membership.web.interceptor;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.membership.common.util.ModelMapUtil;
import cn.membership.common.util.RequestAttrUtil;
import cn.membership.common.util.RequestParamUtil;
import cn.membership.web.constant.CommonConstant;
import cn.membership.web.constant.WebRequestConstant;

/**
 * 基础请求处理拦截器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since
 */
public abstract class BaseRequestInterceptor implements AsyncHandlerInterceptor {

	protected abstract Map<String, Field> getRequestParamFieldMap();

	private void prepareFieldParam(final boolean isListForced, final String fieldName, final Class<?> fieldType, final String[] paramValueArray, final Map<String, Object> allArgMap) throws Exception {
		if (null != fieldType && Class.class != fieldType) {
			if (Long.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Long> longValueList = new ArrayList<Long>();

					for (int i = 0; i < paramValueArray.length; i++) {
						longValueList.add(Long.parseLong(paramValueArray[i]));
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!longValueList.isEmpty()) {
						allArgMap.put(fieldName, longValueList);
					} // if (!longValueList.isEmpty())
				} else {
					System.out.println("=======fieldName=========" + fieldName);
					allArgMap.put(fieldName, Long.parseLong(paramValueArray[0]));
				}
			} else if (Integer.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Integer> integerValueList = new ArrayList<Integer>();

					for (int i = 0; i < paramValueArray.length; i++) {
						integerValueList.add(Integer.parseInt(paramValueArray[i]));
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!integerValueList.isEmpty()) {
						allArgMap.put(fieldName, integerValueList);
					} // if (!integerValueList.isEmpty())
				} else {
					try {
						allArgMap.put(fieldName, Integer.parseInt(paramValueArray[0]));
					} catch (Exception e) {
					}
				}
			} else if (String.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<String> stringValueList = new ArrayList<String>();

					for (int i = 0; i < paramValueArray.length; i++) {
						stringValueList.add(paramValueArray[i]);
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!stringValueList.isEmpty()) {
						allArgMap.put(fieldName, stringValueList);
					} // if (!stringValueList.isEmpty())
				} else {
					allArgMap.put(fieldName, paramValueArray[0]);
				}
			} else if (Boolean.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Boolean> booleanValueList = new ArrayList<Boolean>();

					for (int i = 0; i < paramValueArray.length; i++) {

						if ("1".equals(paramValueArray[i])) {
							booleanValueList.add(true);
						} else {
							booleanValueList.add(false);
						}
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!booleanValueList.isEmpty()) {
						allArgMap.put(fieldName, booleanValueList);
					} // if (!stringValueList.isEmpty())
				} else {
					if ("1".equals(paramValueArray[0])) {
						allArgMap.put(fieldName, true);
					} else {
						allArgMap.put(fieldName, false);
					}
				}
			} else if (Double.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Double> doubleValueList = new ArrayList<Double>();

					for (int i = 0; i < paramValueArray.length; i++) {
						doubleValueList.add(Double.parseDouble(paramValueArray[i]));
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!doubleValueList.isEmpty()) {
						allArgMap.put(fieldName, doubleValueList);
					} // if (!doubleValueList.isEmpty())
				} else {
					allArgMap.put(fieldName, Double.parseDouble(paramValueArray[0]));
				}
			} else if (Float.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Float> floatValueList = new ArrayList<Float>();

					for (int i = 0; i < paramValueArray.length; i++) {
						floatValueList.add(Float.parseFloat(paramValueArray[i]));
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!floatValueList.isEmpty()) {
						allArgMap.put(fieldName, floatValueList);
					} // if (!floatValueList.isEmpty())
				} else {
					allArgMap.put(fieldName, Float.parseFloat(paramValueArray[0]));
				}
			} else if (Date.class == fieldType) {
				if (isListForced || paramValueArray.length > 1) {
					List<Date> dateValueList = new ArrayList<Date>();

					for (int i = 0; i < paramValueArray.length; i++) {
						if (null != paramValueArray[i]) {
							if (8 == paramValueArray[i].length()) {
								dateValueList.add(WebRequestConstant.DEFAULT_DATE_FORMAT.parse(paramValueArray[i]));
							} else if (14 == paramValueArray[i].length()) {
								dateValueList.add(WebRequestConstant.DEFAULT_DATETIME_FORMAT.parse(paramValueArray[i]));
							}
						} // if (null != paramValueArray[i])
					} // for (int i = 0; i < paramValueArray.length; i++)

					if (!dateValueList.isEmpty()) {
						allArgMap.put(fieldName, dateValueList);
					} // if (!dateValueList.isEmpty())
				} else {
					if (null != paramValueArray[0]) {
						if (8 == paramValueArray[0].length()) {
							allArgMap.put(fieldName, WebRequestConstant.DEFAULT_DATE_FORMAT.parse(paramValueArray[0]));
						} else if (14 == paramValueArray[0].length()) {
							allArgMap.put(fieldName, WebRequestConstant.DEFAULT_DATETIME_FORMAT.parse(paramValueArray[0]));
						}
					} // if (null != paramValueArray[0])
				}
			} // else if (Date.class == fieldType)

		} // if (null != fieldType && Class.class != fieldType)
	}

	protected void prepareRequestParams(HttpServletRequest request) throws Exception {

		if (null != request && null == request.getAttribute(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY)) {
			Map<String, Object> allArgMap = new LinkedHashMap<String, Object>();

			final String debugStr = request.getParameter(WebRequestConstant.DEBUG_REQUEST_PARAM_KEY);
			if (null != debugStr) {
				boolean debug = false;
				int debugInt = Integer.parseInt(debugStr);
				if (1 == debugInt) {
					debug = true;
				}
				allArgMap.put(WebRequestConstant.DEBUG_REQUEST_PARAM_KEY, debug);
			} // if (null != debugStr)

			final String randomStr = request.getParameter(WebRequestConstant.RANDOM_REQUEST_PARAM_KEY);
			if (null != randomStr) {
				boolean random = false;
				int randomInt = Integer.parseInt(randomStr);
				if (1 == randomInt) {
					random = true;
				}
				allArgMap.put(WebRequestConstant.RANDOM_REQUEST_PARAM_KEY, random);
			} // if (null != randomStr)

			final String offsetStr = request.getParameter(WebRequestConstant.OFFSET_REQUEST_PARAM_KEY);
			if (null != offsetStr) {
				int offset = Integer.parseInt(offsetStr);
				allArgMap.put(WebRequestConstant.OFFSET_REQUEST_PARAM_KEY, offset);
			} // if (null != offsetStr)

			final String pageSizeStr = request.getParameter(WebRequestConstant.PAGE_SIZE_REQUEST_PARAM_KEY);
			if (null != pageSizeStr) {
				int pageSize = Integer.parseInt(pageSizeStr);
				allArgMap.put(WebRequestConstant.PAGE_SIZE_REQUEST_PARAM_KEY, pageSize);
			} // if (null != pageSizeStr)

			Map<String, Object> sortArgMap = null;
			List<String> sortFieldListParamValue = null;
			String[] sortFieldArray = request.getParameterValues(WebRequestConstant.SORT_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != sortFieldArray && sortFieldArray.length > 0) {
				sortFieldListParamValue = Arrays.asList(sortFieldArray);
				allArgMap.put(WebRequestConstant.SORT_FIELD_LIST_REQUEST_PARAM_KEY, sortFieldListParamValue);
			} // if (null != sortFieldArray && sortFieldArray.length > 0)

			List<String> groupArgList = null;
			List<String> groupArgListParamValue = null;
			String[] groupFieldArray = request.getParameterValues(WebRequestConstant.GROUP_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != groupFieldArray && groupFieldArray.length > 0) {
				groupArgListParamValue = Arrays.asList(groupFieldArray);
				allArgMap.put(WebRequestConstant.GROUP_FIELD_LIST_REQUEST_PARAM_KEY, groupArgListParamValue);
			} // if (null != groupFieldArray && groupFieldArray.length > 0)

			Map<String, Object> rangeArgMap = null;
			List<String> rangeArgListParamValue = null;
			String[] rangeFieldArray = request.getParameterValues(WebRequestConstant.RANGE_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != rangeFieldArray && rangeFieldArray.length > 0) {
				rangeArgListParamValue = Arrays.asList(rangeFieldArray);
				allArgMap.put(WebRequestConstant.RANGE_FIELD_LIST_REQUEST_PARAM_KEY, rangeArgListParamValue);
			} // if (null != rangeFieldArray && rangeFieldArray.length > 0)

			Map<String, Object> likeArgMap = null;
			List<String> likeArgListParamValue = null;
			String[] likeFieldArray = request.getParameterValues(WebRequestConstant.LIKE_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != likeFieldArray && likeFieldArray.length > 0) {
				likeArgListParamValue = Arrays.asList(likeFieldArray);
				allArgMap.put(WebRequestConstant.LIKE_FIELD_LIST_REQUEST_PARAM_KEY, likeArgListParamValue);
			} // if (null != likeFieldArray && likeFieldArray.length > 0)

			Map<String, Object> valueArgMap = null;
			List<String> valueArgListParamValue = null;
			String[] valueFieldArray = request.getParameterValues(WebRequestConstant.VALUE_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != valueFieldArray && valueFieldArray.length > 0) {
				valueArgListParamValue = Arrays.asList(valueFieldArray);
				allArgMap.put(WebRequestConstant.VALUE_FIELD_LIST_REQUEST_PARAM_KEY, valueArgListParamValue);
			} // if (null != valueFieldArray && valueFieldArray.length > 0)

			Map<String, Object> whereArgMap = null;
			List<String> whereArgListParamValue = null;
			String[] whereFieldArray = request.getParameterValues(WebRequestConstant.WHERE_FIELD_LIST_REQUEST_PARAM_KEY);
			if (null != whereFieldArray && whereFieldArray.length > 0) {
				whereArgListParamValue = Arrays.asList(whereFieldArray);
				allArgMap.put(WebRequestConstant.WHERE_FIELD_LIST_REQUEST_PARAM_KEY, whereArgListParamValue);
			} // if (null != whereFieldArray && whereFieldArray.length > 0)

			Map<String, Object> fileArgMap = null;
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				if (null != multipartRequest) {
					Map<String, MultipartFile> multipartFileMap = multipartRequest.getFileMap();
					if (null != multipartFileMap && !multipartFileMap.isEmpty()) {
						for (Map.Entry<String, MultipartFile> multipartFileEntry : multipartFileMap.entrySet()) {
							if (null != multipartFileEntry) {
								final String fileFieldName = multipartFileEntry.getKey();
								final MultipartFile multipartFile = multipartFileEntry.getValue();
								if (null != multipartFile && !StringUtils.isEmpty(fileFieldName)) {

									// TODO: JNSTesting
									String resourceFileDirRealPath;
									resourceFileDirRealPath = CommonConstant.RESOURCE_FILE_SERVER_BASE_PATH_LOCAL;

									final String filename = UUID.randomUUID().toString() + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());

									final File file = new File(resourceFileDirRealPath + filename);
									multipartFile.transferTo(file);

									// System.out.println((file.delete() ?
									// "Success" : "Fail") + " to delete file "
									// + filename );

									if (null == fileArgMap) {
										fileArgMap = new LinkedHashMap<String, Object>();
									} // if (null == fileArgMap)

									Object fileFieldValueObject = allArgMap.get(fileFieldName);
									if (null == fileFieldValueObject) {
										allArgMap.put(fileFieldName, filename);
									} else if (fileFieldValueObject instanceof String) {
										List<String> fileFieldValueList = new ArrayList<String>();
										fileFieldValueList.add((String) fileFieldValueObject);
										fileFieldValueList.add(filename);
										allArgMap.put(fileFieldName, fileFieldValueList);
									} else if (fileFieldValueObject instanceof List) {
										((List) fileFieldValueObject).add(filename);
									} else {
										allArgMap.put(fileFieldName, filename);
									}

									Object fileArgValueObject = fileArgMap.get(fileFieldName);
									if (null == fileArgValueObject) {
										fileArgMap.put(fileFieldName, file);
									} else if (fileArgValueObject instanceof File) {
										List<File> fileArgValueList = new ArrayList<File>();
										fileArgValueList.add((File) fileArgValueObject);
										fileArgValueList.add(file);
										fileArgMap.put(fileFieldName, fileArgValueList);
									} else if (fileArgValueObject instanceof List) {
										((List) fileFieldValueObject).add(file);
									} else {
										fileArgMap.put(fileFieldName, file);
									}

								} // if (null != multipartFile &&
									// !StringUtils.isEmpty(fileFieldName))
							} // if (null != multipartFileEntry)
						} // for (Map.Entry<String,MultipartFile>
							// multipartFileEntry : multipartFileMap.entrySet())

					} // if (null != multipartFileMap &&
						// !multipartFileMap.isEmpty())
				} // if (null != multipartRequest)
			} // if (request instanceof MultipartHttpServletRequest)

			Map<String, String[]> requestParams = request.getParameterMap();
			if (null != requestParams) {

				final Map<String, Field> requestParamFieldMap = getRequestParamFieldMap();
				if (null != requestParamFieldMap && !requestParamFieldMap.isEmpty()) {
					for (Entry<String, Field> requestParamFieldTypeEntry : requestParamFieldMap.entrySet()) {
						if (null != requestParamFieldTypeEntry) {
							final String fieldName = requestParamFieldTypeEntry.getKey();
							if (requestParams.containsKey(fieldName) || requestParams.containsKey(fieldName.concat("[]"))) {

								String[] paramValueArray = requestParams.get(fieldName);
								if (null == paramValueArray) {
									paramValueArray = requestParams.get(fieldName.concat("[]"));
								} // if (null == paramValueArray)

								if (null != paramValueArray && paramValueArray.length > 0) {
									if (null != sortFieldListParamValue && !sortFieldListParamValue.isEmpty() && sortFieldListParamValue.contains(fieldName) && 1 == paramValueArray.length && paramValueArray[0] instanceof String && ("ASC".equalsIgnoreCase(paramValueArray[0]) || "DESC".equalsIgnoreCase(paramValueArray[0]))) {
										allArgMap.put(fieldName, paramValueArray[0]);
									} else {
										final Field field = requestParamFieldTypeEntry.getValue();
										if (null != field) {
											final Class<?> fieldType = field.getType();
											if (null != fieldType && Class.class != fieldType) {
												if (fieldType.isAssignableFrom(List.class)) {
													Type genericType = field.getGenericType();
													if (null != genericType && genericType instanceof ParameterizedType) {
														ParameterizedType parameterizedType = (ParameterizedType) genericType;
														Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
														if (null != actualTypeArguments && 1 == actualTypeArguments.length) {
															if (null != actualTypeArguments[0] && actualTypeArguments[0] instanceof Class) {
																final Class<?> actualTypeArgumentType = (Class<?>) actualTypeArguments[0];
																prepareFieldParam(true, fieldName, actualTypeArgumentType, paramValueArray, allArgMap);
															} // if (null !=
																// actualTypeArguments[0]
																// &&
																// actualTypeArguments[0]
																// instanceof
																// Class)
														} // if (null !=
															// actualTypeArguments
															// &&
															// actualTypeArguments.length
															// > 0)
													} // if (null != genericType
														// && genericType
														// instanceof
														// ParameterizedType)
												} else if (fieldType.isAssignableFrom(Map.class)) {
													// impossible
												} else {
													prepareFieldParam(false, fieldName, fieldType, paramValueArray, allArgMap);
												}
											} // if (null != fieldType &&
												// Class.class != fieldType)
										} // if (null != field)
									} // else
								} // if (null != paramValueArray &&
									// paramValueArray.length > 0)

							} // if (requestParams.containsKey(fieldName) ||
								// requestParams.containsKey(fieldName.concat("[]")))

						} // if (null != requestParamFieldTypeEntry)
					}
				} // if (null != requestParamFieldMap &&
					// !requestParamFieldMap.isEmpty())

				for (Map.Entry<String, String[]> requestParamEntry : requestParams.entrySet()) {

					if (null != requestParamEntry) {
						String requestParamKey = requestParamEntry.getKey();
						if (null != requestParamKey) {
							if (requestParamKey.endsWith("[]")) {
								requestParamKey = requestParamKey.substring(0, requestParamKey.length() - 2);
							} // if (requestParamKey.endsWith("[]"))

							if (null != groupArgListParamValue && !groupArgListParamValue.isEmpty() && groupArgListParamValue.contains(requestParamKey)) {
								if (null == groupArgList) {
									groupArgList = new ArrayList<String>();
								} // if (null == groupArgList)

								groupArgList.add(requestParamKey);
							} // if (null != groupArgListParamValue &&
								// !groupArgListParamValue.isEmpty() &&
								// groupArgListParamValue.contains(requestParamKey))

							String[] requestParamValueArray = requestParamEntry.getValue();
							if (!StringUtils.isEmpty(requestParamKey) && null != requestParamValueArray && null != allArgMap && !allArgMap.containsKey(requestParamKey)) {
								if (requestParamValueArray.length > 1) {
									List<String> requestParamValueList = Arrays.asList(requestParamValueArray);
									allArgMap.put(requestParamKey, requestParamValueList);
								} else if (requestParamValueArray.length > 0) {
									allArgMap.put(requestParamKey, requestParamValueArray[0]);
								}
							} // if (!StringUtils.isEmpty(requestParamKey) &&
								// null != requestParamValueArray && null !=
								// allArgMap &&
								// !allArgMap.containsKey(requestParamKey))

						} // if (null != requestParamKey)

					} // if (null != requestParamEntry)

				} // for (Map.Entry<String, String[]> requestParamEntry :
					// requestParams.entrySet())

			} // if (null != requestParams)

			for (Map.Entry<String, Object> allArgEntry : allArgMap.entrySet()) {
				if (null != allArgEntry) {
					String key = allArgEntry.getKey();
					Object value = allArgEntry.getValue();

					if (null != key && null != value) {
						if (null != sortFieldListParamValue && sortFieldListParamValue.contains(key)) {
							if (null == sortArgMap) {
								sortArgMap = new LinkedHashMap<String, Object>();
							} // if (null == sortArgMap)
							sortArgMap.put(key, value);
						} else if (null != rangeArgListParamValue && rangeArgListParamValue.contains(key)) {
							if (null == rangeArgMap) {
								rangeArgMap = new HashMap<String, Object>();
							} // if (null == rangeArgMap)
							rangeArgMap.put(key, value);
						} else if (null != likeArgListParamValue && likeArgListParamValue.contains(key)) {
							if (null == likeArgMap) {
								likeArgMap = new HashMap<String, Object>();
							} // if (null == likeArgMap)
							likeArgMap.put(key, value);
						} else if (null != whereArgListParamValue && whereArgListParamValue.contains(key)) {
							if (null == whereArgMap) {
								whereArgMap = new HashMap<String, Object>();
							} // if (null == whereArgMap)
							whereArgMap.put(key, value);
						} else if (null != valueArgListParamValue && valueArgListParamValue.contains(key)) {
							if (null == valueArgMap) {
								valueArgMap = new HashMap<String, Object>();
							} // if (null == valueArgMap)
							valueArgMap.put(key, value);
						} else if (!WebRequestConstant.VALUE_FIELD_LIST_REQUEST_PARAM_KEY.equals(key) && !WebRequestConstant.WHERE_FIELD_LIST_REQUEST_PARAM_KEY.equals(key) && !WebRequestConstant.LIKE_FIELD_LIST_REQUEST_PARAM_KEY.equals(key) && !WebRequestConstant.RANGE_FIELD_LIST_REQUEST_PARAM_KEY.equals(key) && !WebRequestConstant.GROUP_FIELD_LIST_REQUEST_PARAM_KEY.equals(key) && !WebRequestConstant.SORT_FIELD_LIST_REQUEST_PARAM_KEY.equals(key)) {
							if (null == valueArgListParamValue) {
								if (null == valueArgMap) {
									valueArgMap = new HashMap<String, Object>();
								} // if (null == valueArgMap)
								valueArgMap.put(key, value);
							} // if (null == valueArgListParamValue)

							if (null == whereArgListParamValue) {
								if (null == whereArgMap) {
									whereArgMap = new HashMap<String, Object>();
								} // if (null == whereArgMap)
								whereArgMap.put(key, value);
							} // if (null == whereArgListParamValue)
						}
					} // if (null != key && null != value)
				} // if (null != allArgEntry)

			} // for (Map.Entry<String, Object> allArgEntry :
				// allArgMap.entrySet())

			if (null != allArgMap && !allArgMap.isEmpty()) {
				RequestAttrUtil.setRequestAttrMap(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY, Collections.unmodifiableMap(allArgMap), request, null);
				// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY,
				// allArgMap, request, null);

				RequestAttrUtil.setRequestAttrMap(WebRequestConstant.REQUEST_ARG_MAP_REQUEST_ATTR_KEY, new LinkedHashMap<String, Object>(allArgMap), request, null);

				if (null != sortArgMap && !sortArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.SORT_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(sortArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.SORT_ARG_MAP_REQUEST_ATTR_KEY, sortArgMap, request, null);
				} // if (null != sortArgMap && !sortArgMap.isEmpty())

				if (null != groupArgList && !groupArgList.isEmpty()) {
					// RequestAttrUtil.setRequestAttrList(WebRequestConstant.GROUP_ARG_LIST_REQUEST_ATTR_KEY,
					// Collections.unmodifiableList(groupArgList), request,
					// null);
					RequestAttrUtil.setRequestAttrList(WebRequestConstant.GROUP_ARG_LIST_REQUEST_ATTR_KEY, groupArgList, request, null);
				} // if (null != groupArgList && !groupArgList.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(rangeArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, rangeArgMap, request, null);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(likeArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, likeArgMap, request, null);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != valueArgMap && !valueArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.VALUE_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(valueArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.VALUE_ARG_MAP_REQUEST_ATTR_KEY, valueArgMap, request, null);
				} // if (null != valueArgMap && !valueArgMap.isEmpty())

				if (null != whereArgMap && !whereArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(whereArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, whereArgMap, request, null);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != fileArgMap && !fileArgMap.isEmpty()) {
					// RequestAttrUtil.setRequestAttrMap(WebRequestConstant.FILE_ARG_MAP_REQUEST_ATTR_KEY,
					// Collections.unmodifiableMap(fileArgMap), request, null);
					RequestAttrUtil.setRequestAttrMap(WebRequestConstant.FILE_ARG_MAP_REQUEST_ATTR_KEY, fileArgMap, request, null);
				} // if (null != fileArgMap && !fileArgMap.isEmpty())

			} // if (null != allArgMap && !allArgMap.isEmpty())

		} // if (null != request && null ==
			// request.getAttribute(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY))
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		boolean isPassed = true;

		int status = WebRequestConstant.SUCCESS_STATUS;
		String message = WebRequestConstant.SUCCESS_MESSAGE;

		if (null != request) {

			try {

				// 阿里云 TODO
				if (WebRequestConstant.HOST.isEmpty()) {
					WebRequestConstant.HOST.put("LOCAL", request.getRemoteHost());
				}

				prepareRequestParams(request);

				status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, null, status);
				message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, null, message);

				if (0 == status) {

				} else {
					isPassed = false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				isPassed = false;
				status = WebRequestConstant.EXCEPTION_STATUS;
				message = WebRequestConstant.EXCEPTION_MESSAGE;
				if (null != e.getLocalizedMessage()) {
					message += " " + e.getLocalizedMessage();
				} // if (null != e.getLocalizedMessage())
			}

			ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, null);
			ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, null);

		} // if (null != request)

		if (0 != status) {
			isPassed = false;
		}

		return isPassed;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (null != modelAndView) {
			final ModelMap modelMap = modelAndView.getModelMap();

			int status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, WebRequestConstant.SUCCESS_STATUS);
			String message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, WebRequestConstant.SUCCESS_MESSAGE);

			// ModelMapUtil.setModelMapString(WebRequestConstant.REQUEST_BASE_URL_MODEL_MAP_KEY,
			// RequestParamUtil.getRequestBaseUrl(request, modelMap), request,
			// modelMap);
			// ModelMapUtil.setModelMapString(WebRequestConstant.REQUEST_URL_MODEL_MAP_KEY,
			// RequestParamUtil.getRequestUrl(request, modelMap), request,
			// modelMap);
			// ModelMapUtil.setModelMapString(WebRequestConstant.REQUEST_QUERY_STRING_MODEL_MAP_KEY,
			// RequestParamUtil.getRequestQueryString(request, modelMap),
			// request, modelMap);
			// ModelMapUtil.setModelMapMap(WebRequestConstant.REQUEST_PARAMS_MODEL_MAP_KEY,
			// RequestParamUtil.getRequestParams(request, modelMap), request,
			// modelMap);

			boolean debug = RequestParamUtil.getRequestParamBoolean(WebRequestConstant.DEBUG_REQUEST_PARAM_KEY, request, modelMap, false);
			if (debug) {

				Map<String, Object> debugMap = new HashMap<String, Object>();
				Map<String, Object> fileArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.FILE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
				Map<String, Object> valueArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.VALUE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
				Map<String, Object> whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
				Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
				Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
				List<Object> groupArgList = RequestAttrUtil.getRequestAttrList(WebRequestConstant.GROUP_ARG_LIST_REQUEST_ATTR_KEY, request, modelMap);
				Map<String, Object> sortArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.SORT_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);

				Map<String, Object> requestInfo = new HashMap<String, Object>();
				requestInfo.put("AuthType", request.getAuthType());
				requestInfo.put("CharacterEncoding", request.getCharacterEncoding());
				requestInfo.put("ContentLength", request.getContentLength());
				requestInfo.put("ContentType", request.getContentType());
				requestInfo.put("ContextPath", request.getContextPath());
				requestInfo.put("LocalAddr", request.getLocalAddr());
				requestInfo.put("LocalName", request.getLocalName());
				requestInfo.put("LocalPort", request.getLocalPort());
				requestInfo.put("Method", request.getMethod());
				requestInfo.put("PathInfo", request.getPathInfo());
				requestInfo.put("PathTranslated", request.getPathTranslated());
				requestInfo.put("Protocol", request.getProtocol());
				requestInfo.put("QueryString", request.getQueryString());
				requestInfo.put("RemoteAddr", request.getRemoteAddr());
				requestInfo.put("RemoteHost", request.getRemoteHost());
				requestInfo.put("RemotePort", request.getRemotePort());
				requestInfo.put("RemoteUser", request.getRemoteUser());
				requestInfo.put("RequestedSessionId", request.getRequestedSessionId());
				requestInfo.put("RequestURI", request.getRequestURI());
				requestInfo.put("RequestURL", request.getRequestURL());
				requestInfo.put("Scheme", request.getScheme());
				requestInfo.put("ServerName", request.getServerName());
				requestInfo.put("ServerPort", request.getServerPort());
				requestInfo.put("ServletPath", request.getServletPath());
				requestInfo.put("ParameterMap", request.getParameterMap());

				debugMap.put("fileArgMap", fileArgMap);
				debugMap.put("valueArgMap", valueArgMap);
				debugMap.put("whereArgMap", whereArgMap);
				debugMap.put("likeArgMap", likeArgMap);
				debugMap.put("rangeArgMap", rangeArgMap);
				debugMap.put("groupArgList", groupArgList);
				debugMap.put("sortArgMap", sortArgMap);
				debugMap.put("requestInfo", requestInfo);
				System.out.println("debugMap = " + debugMap);
				ModelMapUtil.setModelMapMap("debugInfo", debugMap, request, modelMap);
			} // if (debug)

			if (0 == status) {

			} else {

			}
			response.setHeader(WebRequestConstant.SERVER_TIME_MODEL_MAP_KEY, WebRequestConstant.DEFAULT_DATETIME_FORMAT.format(new Date()));
			ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
			ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
			if (!ModelMapUtil.isModelMapObjectAvailable(Map.class, WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap)) {
				ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, new HashMap<String, Object>(), request, modelMap);
			} // if (!ModelMapUtil.isModelMapObjectAvailable(Map.class,
				// WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap))

		} // if (null != modelAndView)

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	}

}
